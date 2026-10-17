package com.cuttingAPI.domain.strategy.placement;

import com.cuttingAPI.domain.model.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PlacementStrategy Heuristics Unit Tests")
class PlacementStrategyTest {

  private final PlacementStrategy bssf = new BestShortSideFit();
  private final PlacementStrategy blsf = new BestLongSideFit();
  private final PlacementStrategy baf = new BestAreaFit();

  @Test
  @DisplayName("Should return Long.MAX_VALUE if piece exceeds free rectangle dimensions")
  void shouldReturnMax_WhenPieceTooBig() {
    Rectangle free = new Rectangle(0, 0, 50, 50);
    Rectangle piece = new Rectangle(0, 0, 60, 40);

    assertEquals(Long.MAX_VALUE, bssf.evaluateScore(piece, free));
    assertEquals(Long.MAX_VALUE, blsf.evaluateScore(piece, free));
    assertEquals(Long.MAX_VALUE, baf.evaluateScore(piece, free));
  }

  @Test
  @DisplayName("BestShortSideFit: Should minimize short side remainder")
  void testBestShortSideFit() {
    Rectangle free = new Rectangle(0, 0, 100, 100);
    Rectangle pieceA = new Rectangle(0, 0, 90, 50); // Remainder: 10, 50 -> Primary: 10
    Rectangle pieceB = new Rectangle(0, 0, 80, 70); // Remainder: 20, 30 -> Primary: 20

    long scoreA = bssf.evaluateScore(pieceA, free);
    long scoreB = bssf.evaluateScore(pieceB, free);

    assertTrue(scoreA < scoreB, "Piece A with smaller short-side remainder should score better");
  }

  @Test
  @DisplayName("BestLongSideFit: Should minimize long side remainder")
  void testBestLongSideFit() {
    Rectangle free = new Rectangle(0, 0, 100, 100);
    Rectangle pieceA = new Rectangle(0, 0, 90, 50); // Remainder: 10, 50 -> Primary: 50
    Rectangle pieceB = new Rectangle(0, 0, 80, 70); // Remainder: 20, 30 -> Primary: 30

    long scoreA = blsf.evaluateScore(pieceA, free);
    long scoreB = blsf.evaluateScore(pieceB, free);

    assertTrue(scoreB < scoreA, "Piece B with smaller long-side remainder should score better");
  }

  @Test
  @DisplayName("BestAreaFit: Should minimize remaining wasted area")
  void testBestAreaFit() {
    Rectangle free = new Rectangle(0, 0, 100, 100); // Area 10,000
    Rectangle pieceA = new Rectangle(0, 0, 90, 90); // Area 8,100 -> Primary: 1,900
    Rectangle pieceB = new Rectangle(0, 0, 50, 50); // Area 2,500 -> Primary: 7,500

    long scoreA = baf.evaluateScore(pieceA, free);
    long scoreB = baf.evaluateScore(pieceB, free);

    assertTrue(scoreA < scoreB, "Piece A filling more area should score better");
  }
}
