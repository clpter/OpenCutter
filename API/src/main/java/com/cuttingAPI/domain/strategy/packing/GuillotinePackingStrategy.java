package com.cuttingAPI.domain.strategy.packing;

import com.cuttingAPI.domain.model.CuttingList;
import com.cuttingAPI.domain.model.FitResult;
import com.cuttingAPI.domain.model.Rectangle;
import com.cuttingAPI.domain.strategy.placement.BestShortSideFit;
import com.cuttingAPI.domain.strategy.placement.PlacementStrategy;

import com.cuttingAPI.domain.strategy.split.SplitRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 2D Guillotine Bin Packing Strategy.
 * High-performance, clean implementation adhering to KISS principles.
 * Supports configurable placement heuristics, functional split rules,
 * 2-step look-ahead search, and transitive waste off-cut merging.
 */
public class GuillotinePackingStrategy implements PackingStrategy {

  /** Micro-strategy for scoring piece placement options. */
  private final PlacementStrategy placementStrategy;

  /** Functional split rule deciding cut axis partitioning. */
  private final SplitRule splitRule;

  /** Flag to sort candidate pieces before packing. */
  private final boolean sortPieces;

  /** Flag enabling 2-step look-ahead search to eliminate greedy traps. */
  private final boolean useLookAhead;

  /** Flag */
  private final boolean preferVerticalCuts;

  /** Flag */
  private final int maxCuttingLength;

  /** Default constructor initializing BestShortSideFit and Dynamic Split Rule. */
  public GuillotinePackingStrategy() {
    this(new BestShortSideFit(), SplitRule.DYNAMIC_SIZE, true, true, false, Integer.MAX_VALUE);
  }

  /**
   * Full constructor configuring placement strategy, split rule, sorting, and
   * look-ahead.
   *
   * @param placementStrategy   micro-strategy scoring heuristic
   * @param splitRule           functional split rule strategy
   * @param sortPieces          flag to pre-sort candidate pieces
   * @param useLookAhead        flag to evaluate 2-step follow-up placement scores
   * @param prefereVerticalCuts
   * @param maxCuttingLength
   */
  public GuillotinePackingStrategy(PlacementStrategy placementStrategy, SplitRule splitRule, boolean sortPieces,
      boolean useLookAhead, boolean prefereVerticalCuts, int maxCuttingLength) {
    this.placementStrategy = placementStrategy;
    this.splitRule = splitRule;
    this.sortPieces = sortPieces;
    this.useLookAhead = useLookAhead;
    this.preferVerticalCuts = prefereVerticalCuts;
    this.maxCuttingLength = maxCuttingLength;
  }

  /**
   * Packs candidate pieces into stock sheets using 2D Guillotine cutting rules.
   *
   * @param pieces        candidate pieces to pack
   * @param stock         available stock sheets
   * @param allowRotation flag allowing 90-degree rotation
   * @return populated CuttingList domain aggregate containing placed positions
   *         and waste off-cuts
   */
  @Override
  public CuttingList pack(List<Rectangle> pieces, List<Rectangle> stock, boolean allowRotation) {
    CuttingList result = new CuttingList();
    if (pieces == null || pieces.isEmpty() || stock == null || stock.isEmpty()) {
      result.setStock(stock);
      result.setPiecesWithoutRotation(pieces);
      return result;
    }

    result.setStock(stock);
    result.sortStock();
    List<Rectangle> remainingStock = result.getStock();

    if (allowRotation) {
      result.setPiecesWithRotation(pieces);
    } else {
      result.setPiecesWithoutRotation(pieces);
    }
    List<Rectangle> remainingPieces = result.getPieces();

    if (this.sortPieces) {
      result.sortPieces();
    }

    for (Rectangle currentStock : remainingStock) {
      if (remainingPieces.isEmpty()) {
        break;
      }

      List<Rectangle> freeRectangles = new ArrayList<>();
      freeRectangles.add(
          new Rectangle(currentStock, 0, 0, currentStock.getId()));

      while (!remainingPieces.isEmpty() && !freeRectangles.isEmpty()) {
        FitResult bestFit = findBestFit(remainingPieces, freeRectangles, currentStock.getHeight());
        if (bestFit == null) {
          break;
        }

        Rectangle chosenPiece = bestFit.piece;
        Rectangle chosenFree = bestFit.freeRect;

        Rectangle position = new Rectangle(chosenPiece, chosenFree.getX(), chosenFree.getY(), currentStock.getId());
        result.addPlacedPiece(position);

        freeRectangles.remove(chosenFree);
        splitFreeRectangle(chosenFree, position.getWidth(), position.getHeight(), freeRectangles,
            currentStock.getHeight());
        mergeFreeRectangles(freeRectangles);
      }

      Collections.sort(freeRectangles);
      for (Rectangle free : freeRectangles) {
        result.addWastePiece(free, currentStock.getId());
      }
    }

    return result;
  }

  /**
   * Evaluates all candidate pieces against available free rectangles to find the
   * best placement fit.
   *
   * @param pieces         unplaced candidate pieces
   * @param freeRectangles available free space rectangles
   * @return optimal FitResult or null if no piece can fit
   */
  private FitResult findBestFit(List<Rectangle> pieces, List<Rectangle> freeRectangles, int stockHeight) {
    Rectangle bestPiece = null;
    Rectangle bestFree = null;
    long bestScore = Long.MAX_VALUE;

    for (Rectangle piece : pieces) {
      for (Rectangle free : freeRectangles) {
        long score = placementStrategy.evaluateScore(piece, free);
        if (score == Long.MAX_VALUE) {
          continue;
        }

        if (useLookAhead && pieces.size() > 1) {
          long lookAhead = evaluateLookAheadStep(piece, free, pieces, freeRectangles, stockHeight);
          score = (score / 2) + (lookAhead / 2);
        }

        if (score < bestScore) {
          bestScore = score;
          bestPiece = piece;
          bestFree = free;
          if (bestScore == 0) {
            break;
          }
        }
      }
      if (bestScore == 0) {
        break;
      }
    }

    return bestPiece != null ? new FitResult(bestPiece, bestFree, bestScore) : null;
  }

  /**
   * Evaluates 2-step follow-up placement scores on simulated free rectangles
   * after a candidate placement.
   *
   * @param candidatePiece  candidate piece being evaluated
   * @param candidateFree   target free rectangle space
   * @param remainingPieces unplaced candidate pieces
   * @param freeRectangles  current free rectangles
   * @return follow-up placement score or 1,000,000 penalty score if dead-end
   */
  private long evaluateLookAheadStep(Rectangle candidatePiece, Rectangle candidateFree,
      List<Rectangle> remainingPieces, List<Rectangle> freeRectangles, int stockHeight) {
    int placedWidth = candidatePiece.getWidth();
    int placedHeight = candidatePiece.getHeight();

    // Deep copy free rectangles so merge simulation does not mutate originals
    List<Rectangle> simulatedFree = new ArrayList<>(freeRectangles.size());
    for (Rectangle r : freeRectangles) {
      if (r != candidateFree) {
        simulatedFree.add(new Rectangle(r));
      }
    }
    splitFreeRectangle(candidateFree, placedWidth, placedHeight, simulatedFree, stockHeight);
    mergeFreeRectangles(simulatedFree);

    long bestNextScore = Long.MAX_VALUE;

    for (Rectangle p2 : remainingPieces) {
      if (p2.getId() == candidatePiece.getId()) {
        continue;
      }
      for (Rectangle free2 : simulatedFree) {
        long score = placementStrategy.evaluateScore(p2, free2);
        if (score == Long.MAX_VALUE) {
          continue;
        }
        if (score < bestNextScore) {
          bestNextScore = score;
          if (bestNextScore == 0) {
            break;
          }
        }
      }
      if (bestNextScore == 0) {
        break;
      }
    }

    return bestNextScore != Long.MAX_VALUE ? bestNextScore : 1000000L;
  }

  /**
   * Splits a free rectangle into two sub-rectangles (right strip and top strip)
   * using the selected SplitRule.
   *
   * @param free           free rectangle being split
   * @param placedWidth    width of placed piece
   * @param placedHeight   height of placed piece
   * @param freeRectangles target free rectangle list to add sub-rectangles to
   */
  private void splitFreeRectangle(Rectangle free, int placedWidth, int placedHeight, List<Rectangle> freeRectangles,
      int stockHeight) {
    int rightWidth = free.getWidth() - placedWidth;
    int topHeight = free.getHeight() - placedHeight;
    boolean splitHorizontal = false;
    if (preferVerticalCuts && free.getHeight() == stockHeight) {
      splitHorizontal = false;
    } else if (free.getHeight() > maxCuttingLength) {
      splitHorizontal = true;
    } else if (free.getWidth() > maxCuttingLength) {
      splitHorizontal = false;
    } else {
      splitHorizontal = splitRule.shouldSplitHorizontal(placedWidth, placedHeight, free, rightWidth, topHeight);
    }
    int rightHeight = splitHorizontal ? placedHeight : free.getHeight();
    int topWidth = splitHorizontal ? free.getWidth() : placedWidth;

    if (rightWidth > 0) {
      freeRectangles.add(new Rectangle(free, free.getX() + placedWidth, free.getY(), rightWidth, rightHeight));
    }
    if (topHeight > 0) {
      freeRectangles.add(new Rectangle(free, free.getX(), free.getY() + placedHeight, topWidth, topHeight));
    }

  }

  /**
   * Merges touching, aligned adjacent free sub-rectangles iteratively until zero
   * mergeable pairs remain.
   *
   * @param freeRectangles free rectangle collection to merge in-place
   */
  private void mergeFreeRectangles(List<Rectangle> freeRectangles) {
    boolean merged = true;
    while (merged) {
      merged = false;
      for (int i = 0; i < freeRectangles.size(); i++) {
        for (int j = i + 1; j < freeRectangles.size(); j++) {
          if (freeRectangles.get(i).tryMerge(freeRectangles.get(j), this.preferVerticalCuts)) {
            freeRectangles.remove(j);
            merged = true;
            break;
          }
        }
        if (merged) {
          break;
        }
      }
    }
  }
}
