package com.cuttingAPI.domain.strategy.packing;

import com.cuttingAPI.domain.model.CuttingList;
import com.cuttingAPI.domain.model.Rectangle;
import com.cuttingAPI.domain.strategy.placement.BestShortSideFit;
import com.cuttingAPI.domain.strategy.split.SplitRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GuillotinePackingStrategy Unit Tests")
class GuillotinePackingStrategyTest {

  private final PackingStrategy strategy = new GuillotinePackingStrategy(new BestShortSideFit(), SplitRule.SHORTER_AXIS,
      true, true, false, Integer.MAX_VALUE);

  @Test
  @DisplayName("Should return empty result when pieces or stock is empty")
  void shouldReturnEmptyResult_WhenInputsAreEmpty() {
    CuttingList resultNull = strategy.pack(null, null, true);
    assertNotNull(resultNull);

    CuttingList resultEmpty = strategy.pack(List.of(), List.of(), true);
    assertNotNull(resultEmpty);
    assertTrue(resultEmpty.getPlacedPieces().isEmpty());
  }

  @Test
  @DisplayName("Should pack single piece into single stock sheet")
  void shouldPackSinglePiece_IntoSingleStock() {
    List<Rectangle> stock = List.of(new Rectangle(0, 0, 100, 100, 1));
    List<Rectangle> pieces = List.of(new Rectangle(0, 0, 40, 60, 10, 0, 1));

    CuttingList result = strategy.pack(pieces, stock, true);

    assertEquals(1, result.getPlacedPieces().size());
    Rectangle placed = result.getPlacedPieces().get(0);
    assertEquals(0, placed.getX());
    assertEquals(0, placed.getY());
    assertEquals(40, placed.getWidth());
    assertEquals(60, placed.getHeight());
    assertEquals(10, placed.getId());
    assertEquals(1, placed.getAssignment());
    assertEquals(1, placed.getNumber());
  }

  @Test
  @DisplayName("Should rotate piece when required to fit into stock sheet")
  void shouldRotatePiece_WhenNeededToFit() {
    List<Rectangle> stock = List.of(new Rectangle(0, 0, 50, 100, 1));
    List<Rectangle> pieces = List.of(new Rectangle(0, 0, 80, 40, 10, 0, 1)); // Fits only rotated as 40x80

    CuttingList result = strategy.pack(pieces, stock, true);

    assertEquals(1, result.getPlacedPieces().size());
    Rectangle placed = result.getPlacedPieces().get(0);
    assertEquals(40, placed.getWidth());
    assertEquals(80, placed.getHeight());
  }
}
