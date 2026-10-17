package com.cuttingAPI.domain.strategy.placement;

import com.cuttingAPI.domain.model.Rectangle;

/**
 * Best Area Fit (BAF) Placement Micro-Strategy.
 * Evaluates candidate piece placements by minimizing the remaining area
 * of leftover free space (Primary score: free.getSize() - piece.getSize()).
 * Employs 2-level composite scoring for clean tie-breaking.
 */
public class BestAreaFit implements PlacementStrategy {

  /**
   * Evaluates placement fit score for a candidate piece inside a free space.
   *
   * @param piece candidate piece rectangle
   * @param free available free rectangle space
   * @return composite score (lower is better) or Long.MAX_VALUE if piece cannot fit
   */
  @Override
  public long evaluateScore(Rectangle piece, Rectangle free) {
    if (piece.getWidth() > free.getWidth() || piece.getHeight() > free.getHeight()) {
      return Long.MAX_VALUE;
    }
    long primary = free.getSize() - piece.getSize();
    int leftoverX = free.getWidth() - piece.getWidth();
    int leftoverY = free.getHeight() - piece.getHeight();
    long secondary = Math.min(leftoverX, leftoverY);
    return primary * 1_000_000L + secondary;
  }
}
