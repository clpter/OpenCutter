package com.cuttingAPI.domain.service;

import com.cuttingAPI.domain.model.CuttingList;
import com.cuttingAPI.domain.model.Rectangle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pure Java Domain Service encapsulating metrics calculation, waste analysis,
 * and multi-criterion fitness evaluation for 2D Bin Packing solutions.
 */
public class CuttingListEvaluator {

  /**
   * Calculates total surface area of placed pieces in a solution.
   *
   * @param list target CuttingList solution
   * @return total placed surface area
   */
  public static int getPlacedArea(CuttingList list) {
    if (list == null || list.getPlacedPieces() == null) {
      return 0;
    }
    int total = 0;
    for (Rectangle r : list.getPlacedPieces()) {
      total += r.getSize();
    }
    return total;
  }

  /**
   * Calculates total stock sheet surface area across all available stock sheets.
   *
   * @param list target CuttingList solution
   * @return total stock area
   */
  public static int getTotalStockArea(CuttingList list) {
    if (list == null || list.getStock() == null) {
      return 0;
    }
    int total = 0;
    for (Rectangle r : list.getStock()) {
      total += r.getSize();
    }
    return total;
  }

  /**
   * Calculates total stock sheet surface area of ONLY the stock sheets actually
   * used in placement.
   *
   * @param list target CuttingList solution
   * @return total used stock area
   */
  public static int getUsedStockArea(CuttingList list) {
    if (list == null || list.getPlacedPieces() == null || list.getPlacedPieces().isEmpty()
        || list.getStock() == null || list.getStock().isEmpty()) {
      return 0;
    }
    Set<Integer> usedStockIds = new HashSet<>();
    for (Rectangle p : list.getPlacedPieces()) {
      usedStockIds.add(p.getAssignment());
    }
    int usedArea = 0;
    for (Rectangle s : list.getStock()) {
      if (usedStockIds.contains(s.getId())) {
        usedArea += s.getSize();
      }
    }
    return usedArea > 0 ? usedArea : getTotalStockArea(list);
  }

  /**
   * Calculates the number of raw stock sheets actually used in placement.
   *
   * @param list target CuttingList solution
   * @return number of used stock sheets
   */
  public static int getUsedStockCount(CuttingList list) {
    if (list == null || list.getPlacedPieces() == null || list.getPlacedPieces().isEmpty()) {
      return 0;
    }
    Set<Integer> usedStockIds = new HashSet<>();
    for (Rectangle p : list.getPlacedPieces()) {
      usedStockIds.add(p.getAssignment());
    }
    return usedStockIds.size();
  }

  /**
   * Calculates material fill percentage (placed area / used stock area * 100.0).
   *
   * @param list target CuttingList solution
   * @return material yield percentage
   */
  public static double getMaterialYieldPercentage(CuttingList list) {
    if (list == null) {
      return 0.0;
    }
    int usedArea = getUsedStockArea(list);
    if (usedArea == 0) {
      return 0.0;
    }
    return (double) getPlacedArea(list) / usedArea * 100.0;
  }

  /**
   * Finds the maximum single waste piece size (area) to prioritize large reusable
   * off-cuts.
   *
   * @param list target CuttingList solution
   * @return largest single waste piece area
   */
  public static int getMaxWastePieceSize(CuttingList list) {
    if (list == null || list.getWaste() == null || list.getWaste().isEmpty()) {
      return 0;
    }
    int maxArea = 0;
    for (Rectangle w : list.getWaste()) {
      if (w.getSize() > maxArea) {
        maxArea = w.getSize();
      }
    }
    return maxArea;
  }

  /**
   * Calculates the Quadratic Waste Concentration Score (sum of waste areas
   * squared in m²).
   * Mathematically rewards large contiguous reusable off-cuts and heavily
   * penalizes fragmented micro-strips.
   *
   * @param list target CuttingList solution
   * @return quadratic waste score
   */
  public static double getQuadraticWasteScore(CuttingList list) {
    if (list == null || list.getWaste() == null || list.getWaste().isEmpty()) {
      return 0.0;
    }
    double score = 0.0;
    for (Rectangle w : list.getWaste()) {
      double areaInM2 = (double) w.getSize() / 1_000_000.0;
      score += areaInM2 * areaInM2;
    }
    return score;
  }

  /**
   * Filters waste off-cuts that satisfy minimum usable woodworking panel
   * dimensions.
   *
   * @param list         target CuttingList solution
   * @param minDimension minimum width or height threshold in mm (e.g. 100mm)
   * @return list of usable waste panel rectangles
   */
  public static List<Rectangle> getUsableWastePieces(CuttingList list, int minDimension) {
    if (list == null || list.getWaste() == null) {
      return new ArrayList<>();
    }
    List<Rectangle> usable = new ArrayList<>();
    for (Rectangle w : list.getWaste()) {
      if (w.getWidth() >= minDimension && w.getHeight() >= minDimension) {
        usable.add(w);
      }
    }
    return usable;
  }

  /**
   * Calculates total residual waste surface area across waste off-cut panels.
   *
   * @param list target CuttingList solution
   * @return total waste area
   */
  public static int calculateWaste(CuttingList list) {
    if (list == null || list.getWaste() == null) {
      return 0;
    }
    int total = 0;
    for (Rectangle item : list.getWaste()) {
      total += item.getSize();
    }
    return total;
  }

  /**
   * Domain Fitness Evaluation: Adaptive Two-State Comparison between solution A
   * and solution B.
   *
   * 1. Completion Priority: Solution placing ALL pieces always beats an
   * incomplete solution.
   * 2. Incomplete State: When pieces remain unplaced, prioritize maximum placed
   * surface area (getPlacedArea()),
   * cramming as much valuable wood onto the boards as possible.
   * 3. Complete State & Ties: Minimize raw stock area used, then maximize
   * Quadratic Waste Concentration Score
   * and Max Waste Panel Size to consolidate residual off-cuts into large reusable
   * panels.
   *
   * @param a candidate solution A
   * @param b candidate solution B
   * @return true if solution A is strictly better than solution B
   */
  public static boolean isBetterThan(CuttingList a, CuttingList b) {
    if (a == null) {
      return false;
    }
    if (b == null) {
      return true;
    }

    boolean aComplete = a.getPieces() == null || a.getPieces().isEmpty();
    boolean bComplete = b.getPieces() == null || b.getPieces().isEmpty();

    // Tier 1: Completion Priority (placed ALL candidate pieces beats incomplete)
    if (aComplete != bComplete) {
      return aComplete;
    }

    // Tier 2: Incomplete State Priority - Maximize total placed surface area
    if (!aComplete) {
      int aPlacedArea = getPlacedArea(a);
      int bPlacedArea = getPlacedArea(b);
      if (aPlacedArea != bPlacedArea) {
        return aPlacedArea > bPlacedArea;
      }

      int aPlacedCount = a.getPlacedPieces().size();
      int bPlacedCount = b.getPlacedPieces().size();
      if (aPlacedCount != bPlacedCount) {
        return aPlacedCount > bPlacedCount;
      }
    }

    // Tier 3: Minimize raw stock area consumed (fewer raw boards wins)
    int aUsedArea = getUsedStockArea(a);
    int bUsedArea = getUsedStockArea(b);
    if (aUsedArea != bUsedArea) {
      return aUsedArea < bUsedArea;
    }

    // Tier 4: Quadratic Waste Concentration Score (heavily rewards big contiguous
    // off-cuts)
    double aQuad = getQuadraticWasteScore(a);
    double bQuad = getQuadraticWasteScore(b);
    if (Math.abs(aQuad - bQuad) > 0.0001) {
      return aQuad > bQuad;
    }

    // Tier 5: Max single waste panel size
    int aMaxWaste = getMaxWastePieceSize(a);
    int bMaxWaste = getMaxWastePieceSize(b);
    if (aMaxWaste != bMaxWaste) {
      return aMaxWaste > bMaxWaste;
    }

    // Tier 6: Minimize total number of fragmented waste pieces
    return a.getWaste().size() < b.getWaste().size();
  }
}
