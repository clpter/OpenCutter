package com.cuttingAPI.domain.strategy.split;

import com.cuttingAPI.domain.model.Rectangle;

/**
 * Functional Strategy Enum defining how a free rectangle is split
 * into remaining sub-rectangles after placing a candidate piece in 2D Guillotine packing.
 */
public enum SplitRule {

  /** Splits along the shorter axis of remaining waste. */
  SHORTER_AXIS((w, h, free, rw, th) -> rw <= th),

  /** Splits along the longer axis of remaining waste. */
  LONGER_AXIS((w, h, free, rw, th) -> rw >= th),

  /** Splits to maximize the total area of the largest remaining sub-rectangle. */
  MAXIMIZE_AREA((w, h, free, rw, th) -> rw * h >= free.getWidth() * th),

  /** Dynamically selects split axis based on piece-to-panel area ratio (>= 25% larger axis, < 25% shorter axis). */
  DYNAMIC_SIZE((w, h, free, rw, th) -> {
    double areaRatio = (double) (w * h) / free.getSize();
    return areaRatio >= 0.25 ? (rw >= th) : (rw <= th);
  });

  /**
   * Functional interface for split evaluation logic.
   */
  @FunctionalInterface
  public interface SplitEvaluator {
    boolean isHorizontalSplit(int placedWidth, int placedHeight, Rectangle free, int rightWidth, int topHeight);
  }

  private final SplitEvaluator evaluator;

  SplitRule(SplitEvaluator evaluator) {
    this.evaluator = evaluator;
  }

  /**
   * Determines whether to split the free space along the horizontal or vertical axis.
   *
   * @param placedWidth width of placed piece
   * @param placedHeight height of placed piece
   * @param free free rectangle space being cut
   * @param rightWidth width of right sub-rectangle
   * @param topHeight height of top sub-rectangle
   * @return true for horizontal split, false for vertical split
   */
  public boolean shouldSplitHorizontal(int placedWidth, int placedHeight, Rectangle free, int rightWidth, int topHeight) {
    return evaluator.isHorizontalSplit(placedWidth, placedHeight, free, rightWidth, topHeight);
  }
}
