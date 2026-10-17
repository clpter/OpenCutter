package com.cuttingAPI.domain.strategy.placement;

import com.cuttingAPI.domain.model.Rectangle;

/**
 * Micro-strategy interface for scoring how well a piece fits into a candidate free rectangle.
 * Returns a 2-level composite score (primary * 1_000_000L + secondary) to break ties cleanly.
 * Lower scores indicate a better fit. Returns Long.MAX_VALUE if the piece cannot fit.
 */
public interface PlacementStrategy {
  long evaluateScore(Rectangle piece, Rectangle free);
}
