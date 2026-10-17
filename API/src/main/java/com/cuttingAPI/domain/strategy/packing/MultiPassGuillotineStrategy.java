package com.cuttingAPI.domain.strategy.packing;

import com.cuttingAPI.domain.model.CuttingList;
import com.cuttingAPI.domain.model.Rectangle;
import com.cuttingAPI.domain.strategy.placement.BestAreaFit;
import com.cuttingAPI.domain.strategy.placement.BestLongSideFit;
import com.cuttingAPI.domain.strategy.placement.BestShortSideFit;
import com.cuttingAPI.domain.strategy.placement.PlacementStrategy;
import com.cuttingAPI.domain.strategy.split.SplitRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Multi-Pass Guillotine Bin Packing Strategy.
 * Evaluates systematic multi-criterion sorting (Area, Width, Height, Grouped),
 * Exact Dimension-Clustering Permutations (batching identical width/height
 * items),
 * Group-Local Size Permutations (shuffling within size buckets),
 * placement heuristics, dynamic split rules, and 2-step look-ahead.
 */
public class MultiPassGuillotineStrategy implements PackingStrategy {

  /**
   * Maximum number of randomized piece order permutations to evaluate in Pass 2.
   */
  private final int permutations;

  /**
   * Maximum candidate piece count threshold for enabling 2-step look-ahead
   * search.
   * Look-ahead search is active when candidate piece count is <=
   * maxPiecesForLookAhead.
   */
  private final int maxPiecesForLookAhead;

  /** Deterministic random number generator seeded for reproducible benchmarks. */
  private final Random random = new Random(42);

  /** Flag */
  private final boolean preferVerticalCuts;

  /** Flag */
  private final int maxCuttingLength;

  /**
   * Default constructor initializing 30 permutations and look-ahead threshold of
   * 50 pieces
   * for optimal balance of quality and speed.
   */
  public MultiPassGuillotineStrategy() {
    this(30, 50, false, Integer.MAX_VALUE);
  }

  /**
   * Full constructor configuring permutation count and look-ahead piece
   * threshold.
   *
   * @param permutations          number of shuffled piece order permutations
   *                              (minimum 1)
   * @param maxPiecesForLookAhead maximum piece count threshold for enabling
   *                              look-ahead search
   * @param preferVerticalCuts    Flag
   * @param maxCuttingLength      Flag
   */
  public MultiPassGuillotineStrategy(int permutations, int maxPiecesForLookAhead, boolean preferVerticalCuts,
      int maxCuttingLength) {
    this.permutations = Math.max(1, permutations);
    this.maxPiecesForLookAhead = Math.max(0, maxPiecesForLookAhead);

    this.preferVerticalCuts = preferVerticalCuts;
    this.maxCuttingLength = maxCuttingLength;
  }

  /**
   * Executes multi-pass guillotine bin packing optimization.
   *
   * @param pieces        candidate pieces to pack
   * @param stock         available stock sheets
   * @param allowRotation flag allowing 90-degree rotation
   * @return optimal CuttingList solution aggregate across all passes
   */
  @Override
  public CuttingList pack(List<Rectangle> pieces, List<Rectangle> stock, boolean allowRotation) {
    if (pieces == null || stock == null || pieces.isEmpty() || stock.isEmpty()) {
      return new GuillotinePackingStrategy().pack(pieces, stock, allowRotation);
    }

    boolean enableLookAhead = pieces.size() <= this.maxPiecesForLookAhead;

    List<PlacementStrategy> systematicPlacementStrategies = List.of(
        new BestShortSideFit(),
        new BestLongSideFit(),
        new BestAreaFit());

    PlacementStrategy primaryStrategy = new BestShortSideFit();
    CuttingList bestResult = null;

    // -----------------------------------------------------------------------
    // Pass 1: Systematic Search across Placement Heuristics & Split Rules
    // -----------------------------------------------------------------------
    boolean[] lookAheadModes = enableLookAhead ? new boolean[] { true, false } : new boolean[] { false };

    for (PlacementStrategy ps : systematicPlacementStrategies) {
      for (SplitRule sr : SplitRule.values()) {
        for (boolean lookAhead : lookAheadModes) {
          GuillotinePackingStrategy strategy = new GuillotinePackingStrategy(ps, sr, true, lookAhead,
              preferVerticalCuts, maxCuttingLength);
          CuttingList candidate = strategy.pack(pieces, stock, allowRotation);

          if (candidate.isBetterThan(bestResult)) {
            bestResult = candidate;
          }
        }
      }
    }

    // -----------------------------------------------------------------------
    // Pass 2: Sequence Permutations Pipeline (Presorts, Dimension Clusters, Size
    // Buckets & Random Shuffles)
    // -----------------------------------------------------------------------
    List<List<Rectangle>> sequenceCandidates = new ArrayList<>();

    // A. Multi-Criterion Presorts (Width, Height, Area, Longest Side, Grouped)
    sequenceCandidates.addAll(generateMultiCriterionSorts(pieces));

    // B. Exact Dimension-Clustering Permutations
    int dimensionClusterCount = Math.max(1, permutations / 3);
    for (int i = 0; i < dimensionClusterCount; i++) {
      sequenceCandidates.add(generateExactDimensionClusterPermutation(pieces, i % 2 == 0));
    }

    // C. Grouped Size Bucket Permutations
    int sizeGroupedCount = Math.max(1, permutations / 3);
    for (int i = 0; i < sizeGroupedCount; i++) {
      sequenceCandidates.add(generateGroupShuffledPieces(pieces));
    }

    // D. Pure Random Shuffled Permutations
    int randomCount = Math.max(1, permutations - dimensionClusterCount - sizeGroupedCount);
    for (int i = 0; i < randomCount; i++) {
      List<Rectangle> shuffled = new ArrayList<>();
      for (Rectangle p : pieces) {
        shuffled.add(new Rectangle(p));
      }
      Collections.shuffle(shuffled, random);
      sequenceCandidates.add(shuffled);
    }

    // Evaluate all sequence candidates with primary BestShortSideFit strategy
    GuillotinePackingStrategy permutationStrategy = new GuillotinePackingStrategy(
        primaryStrategy, SplitRule.DYNAMIC_SIZE, false, enableLookAhead, preferVerticalCuts, maxCuttingLength);

    for (List<Rectangle> sequence : sequenceCandidates) {
      CuttingList candidate = permutationStrategy.pack(sequence, stock, allowRotation);
      if (candidate.isBetterThan(bestResult)) {
        bestResult = candidate;
      }
    }

    return bestResult;
  }

  /**
   * Generates systematic multi-criterion sorting variations as plain sorted
   * lists.
   * 1. Area Descending
   * 2. Longest Side Descending
   * 3. Width Descending
   * 4. Height Descending
   * 5. Grouped Width (width desc, then height desc)
   * 6. Grouped Height (height desc, then width desc)
   */
  private List<List<Rectangle>> generateMultiCriterionSorts(List<Rectangle> basePieces) {
    List<List<Rectangle>> result = new ArrayList<>();

    // 1. Area Descending
    result.add(sortedCopy(basePieces, (a, b) -> Integer.compare(b.getSize(), a.getSize())));

    // 2. Longest Side Descending
    result.add(sortedCopy(basePieces,
        (a, b) -> Integer.compare(Math.max(b.getWidth(), b.getHeight()), Math.max(a.getWidth(), a.getHeight()))));

    // 3. Width Descending
    result.add(sortedCopy(basePieces, (a, b) -> Integer.compare(b.getWidth(), a.getWidth())));

    // 4. Height Descending
    result.add(sortedCopy(basePieces, (a, b) -> Integer.compare(b.getHeight(), a.getHeight())));

    // 5. Grouped Width (primary: width desc, secondary: height desc)
    result.add(sortedCopy(basePieces, (a, b) -> {
      int wComp = Integer.compare(b.getWidth(), a.getWidth());
      return wComp != 0 ? wComp : Integer.compare(b.getHeight(), a.getHeight());
    }));

    // 6. Grouped Height (primary: height desc, secondary: width desc)
    result.add(sortedCopy(basePieces, (a, b) -> {
      int hComp = Integer.compare(b.getHeight(), a.getHeight());
      return hComp != 0 ? hComp : Integer.compare(b.getWidth(), a.getWidth());
    }));

    return result;
  }

  /**
   * Creates a deep-copied list sorted by the given comparator.
   *
   * @param source     source piece list
   * @param comparator sorting comparator
   * @return new sorted list of copied rectangles
   */
  private List<Rectangle> sortedCopy(List<Rectangle> source, java.util.Comparator<Rectangle> comparator) {
    List<Rectangle> copy = new ArrayList<>(source.size());
    for (Rectangle r : source) {
      copy.add(new Rectangle(r));
    }
    copy.sort(comparator);
    return copy;
  }

  /**
   * Generates an Exact Dimension-Cluster Permutation.
   * Clusters candidate pieces by exact matching width (or height),
   * orders clusters by total surface area descending, and shuffles within each
   * cluster.
   */
  private List<Rectangle> generateExactDimensionClusterPermutation(List<Rectangle> basePieces, boolean groupByWidth) {
    if (basePieces == null || basePieces.isEmpty()) {
      return new ArrayList<>();
    }

    Map<Integer, List<Rectangle>> clusters = new HashMap<>();
    for (Rectangle p : basePieces) {
      int key = groupByWidth ? p.getWidth() : p.getHeight();
      clusters.computeIfAbsent(key, k -> new ArrayList<>()).add(new Rectangle(p));
    }

    List<Map.Entry<Integer, List<Rectangle>>> sortedClusters = new ArrayList<>(clusters.entrySet());
    sortedClusters.sort((e1, e2) -> {
      int sum1 = e1.getValue().stream().mapToInt(Rectangle::getSize).sum();
      int sum2 = e2.getValue().stream().mapToInt(Rectangle::getSize).sum();
      return Integer.compare(sum2, sum1);
    });

    List<Rectangle> combined = new ArrayList<>(basePieces.size());
    for (Map.Entry<Integer, List<Rectangle>> entry : sortedClusters) {
      List<Rectangle> clusterList = entry.getValue();
      Collections.shuffle(clusterList, random);
      combined.addAll(clusterList);
    }

    return combined;
  }

  /**
   * Generates a Grouped / Clustered Permutation sequence by size buckets.
   * Partitions candidate pieces into 3 size buckets (Large, Medium, Small),
   * shuffles pieces ONLY within each bucket, and concatenates the buckets.
   */
  private List<Rectangle> generateGroupShuffledPieces(List<Rectangle> basePieces) {
    if (basePieces == null || basePieces.isEmpty()) {
      return new ArrayList<>();
    }

    int maxArea = 0;
    for (Rectangle p : basePieces) {
      if (p.getSize() > maxArea) {
        maxArea = p.getSize();
      }
    }

    int largeThreshold = (int) (maxArea * 0.6);
    int mediumThreshold = (int) (maxArea * 0.25);

    List<Rectangle> largeBucket = new ArrayList<>();
    List<Rectangle> mediumBucket = new ArrayList<>();
    List<Rectangle> smallBucket = new ArrayList<>();

    for (Rectangle p : basePieces) {
      Rectangle copy = new Rectangle(p);
      if (copy.getSize() >= largeThreshold) {
        largeBucket.add(copy);
      } else if (copy.getSize() >= mediumThreshold) {
        mediumBucket.add(copy);
      } else {
        smallBucket.add(copy);
      }
    }

    Collections.shuffle(largeBucket, random);
    Collections.shuffle(mediumBucket, random);
    Collections.shuffle(smallBucket, random);

    List<Rectangle> combined = new ArrayList<>(basePieces.size());
    combined.addAll(largeBucket);
    combined.addAll(mediumBucket);
    combined.addAll(smallBucket);
    return combined;
  }
}
