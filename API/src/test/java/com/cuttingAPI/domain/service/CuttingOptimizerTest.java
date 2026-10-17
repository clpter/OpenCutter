package com.cuttingAPI.domain.service;

import com.cuttingAPI.domain.model.CuttingList;
import com.cuttingAPI.domain.model.Rectangle;
import com.cuttingAPI.domain.strategy.packing.GuillotinePackingStrategy;
import com.cuttingAPI.domain.strategy.packing.MultiPassGuillotineStrategy;
import com.cuttingAPI.domain.strategy.packing.PackingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GuillotinePackingStrategy and CuttingOptimizer Domain Unit Tests")
class CuttingOptimizerTest {

  private PackingStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new GuillotinePackingStrategy();
  }

  @Nested
  @DisplayName("GuillotinePackingStrategy Unit Tests")
  class StrategyTests {

    @Test
    @DisplayName("Should fit single piece into matching stock sheet without rotation")
    void shouldPackSinglePiece_FixedDirection() {
      List<Rectangle> stock = List.of(new Rectangle(0, 0, 100, 100, 1));
      List<Rectangle> pieces = List.of(new Rectangle(0, 0, 40, 40, 10));

      CuttingList result = strategy.pack(pieces, stock, false);

      assertNotNull(result);
      assertEquals(1, result.getPlacedPieces().size());
      assertEquals(10, result.getPlacedPieces().get(0).getId());
      assertEquals(0, result.getPlacedPieces().get(0).getX());
      assertEquals(0, result.getPlacedPieces().get(0).getY());
      assertEquals(40, result.getPlacedPieces().get(0).getWidth());
      assertEquals(40, result.getPlacedPieces().get(0).getHeight());
      assertTrue(result.getPieces().isEmpty());
    }

    @Test
    @DisplayName("Should rotate piece when allowRotation is true and piece is rotated 90 degrees to fit")
    void shouldRotatePieceToFit_WhenRotationAllowed() {
      List<Rectangle> stock = List.of(new Rectangle(0, 0, 50, 100, 1));
      List<Rectangle> pieces = List.of(new Rectangle(0, 0, 80, 40, 10)); // Width 80 > 50 stock width, but height 40 <=
                                                                         // 50

      CuttingList result = strategy.pack(pieces, stock, true);

      assertNotNull(result);
      assertEquals(1, result.getPlacedPieces().size());
      assertEquals(10, result.getPlacedPieces().get(0).getId());
      assertEquals(40, result.getPlacedPieces().get(0).getWidth());
      assertEquals(80, result.getPlacedPieces().get(0).getHeight());
      assertTrue(result.getPieces().isEmpty());
    }

    @Test
    @DisplayName("Should leave piece unfitted if piece is larger than stock sheet")
    void shouldLeavePieceUnfitted_WhenPieceExceedsStock() {
      List<Rectangle> stock = List.of(new Rectangle(0, 0, 50, 50, 1));
      List<Rectangle> pieces = List.of(new Rectangle(0, 0, 100, 100, 10));

      CuttingList result = strategy.pack(pieces, stock, false);

      assertNotNull(result);
      assertTrue(result.getPlacedPieces().isEmpty());
      assertEquals(1, result.getPieces().size());
      assertEquals(10, result.getPieces().get(0).getId());
    }

    @Test
    @DisplayName("Should handle empty pieces or stock input gracefully")
    void shouldHandleEmptyInputs() {
      CuttingList result1 = strategy.pack(new ArrayList<>(), List.of(new Rectangle(0, 0, 100, 100)), false);
      assertNotNull(result1);
      assertTrue(result1.getPlacedPieces().isEmpty());

      CuttingList result2 = strategy.pack(List.of(new Rectangle(0, 0, 10, 10)), new ArrayList<>(), false);
      assertNotNull(result2);
      assertEquals(1, result2.getPieces().size());
    }
  }

  @Nested
  @DisplayName("CuttingOptimizer Service Integration Tests")
  class CuttingOptimizerServiceTests {

    @Test
    @DisplayName("cutFixedDirection should call strategy with fixed orientation")
    void shouldExecuteCutFixedDirection() {
      List<Rectangle> stock = List.of(new Rectangle(0, 0, 100, 100, 1));
      List<Rectangle> pieces = List.of(new Rectangle(0, 0, 40, 40, 2));

      CuttingOptimizer optimizer = new CuttingOptimizer(pieces, stock,
          new MultiPassGuillotineStrategy(30, 50, false, Integer.MAX_VALUE));
      optimizer.cutFixedDirection();

      CuttingList result = optimizer.getCuttingList();
      assertNotNull(result);
      assertEquals(1, result.getPlacedPieces().size());
    }

    @Test
    @DisplayName("cutWithRotation should call strategy with rotation enabled")
    void shouldExecuteCutWithRotation() {
      List<Rectangle> stock = List.of(new Rectangle(0, 0, 50, 100, 1));
      List<Rectangle> pieces = List.of(new Rectangle(0, 0, 80, 40, 2));

      CuttingOptimizer optimizer = new CuttingOptimizer(pieces, stock,
          new MultiPassGuillotineStrategy(30, 50, false, Integer.MAX_VALUE));
      optimizer.cutWithRotation();

      CuttingList result = optimizer.getCuttingList();
      assertNotNull(result);
      assertEquals(1, result.getPlacedPieces().size());
    }
  }
}
