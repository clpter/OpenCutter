package com.cuttingAPI.domain.strategy.placement;

import com.cuttingAPI.domain.model.Rectangle;

/**
 * Best Short Side Fit (BSSF) Placement Micro-Strategy.
 * Evaluates candidate piece placements by minimizing the short side remainder
 * of leftover free space (Primary score: min(leftoverX, leftoverY)).
 * Employs 2-level composite scoring (primary * 1_000_000L + secondary) for clean tie-breaking.
 */
public class BestShortSideFit implements PlacementStrategy {

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
    int leftoverX = free.getWidth() - piece.getWidth();
    int leftoverY = free.getHeight() - piece.getHeight();
    long primary = Math.min(leftoverX, leftoverY);
    long secondary = Math.max(leftoverX, leftoverY);
    return primary * 1_000_000L + secondary;
  }
}
