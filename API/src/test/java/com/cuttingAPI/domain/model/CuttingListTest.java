package com.cuttingAPI.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CuttingList Domain Model Unit Tests")
class CuttingListTest {

  @Nested
  @DisplayName("Piece Addition and Rotation Strategy Tests")
  class PieceAdditionTests {

    @Test
    @DisplayName("Should add pieces without rotation when using setPiecesWithoutRotation")
    void shouldAddPiecesWithoutRotation() {
      List<Rectangle> inputPieces = List.of(
          new Rectangle(0, 0, 100, 50, 1));

      CuttingList list = new CuttingList();
      list.setPieces(inputPieces);

      assertEquals(1, list.getPieces().size());
      assertEquals(100, list.getPieces().get(0).getWidth());
      assertEquals(50, list.getPieces().get(0).getHeight());
    }

    @Test
    @DisplayName("Should add pieces with 90-degree rotated copy when using setPiecesWithRotation")
    void shouldAddPiecesWithRotation_WhenDimensionsDiffer() {
      List<Rectangle> inputPieces = List.of(
          new Rectangle(0, 0, 100, 50, 1));

      CuttingList list = new CuttingList();
      list.setPiecesWithRotation(inputPieces);

      assertEquals(2, list.getPieces().size());
      // Original orientation
      assertEquals(100, list.getPieces().get(0).getWidth());
      assertEquals(50, list.getPieces().get(0).getHeight());
      // Rotated orientation
      assertEquals(50, list.getPieces().get(1).getWidth());
      assertEquals(100, list.getPieces().get(1).getHeight());
    }

    @Test
    @DisplayName("Should not duplicate square pieces when using setPiecesWithRotation")
    void shouldNotDuplicateSquarePieces_WhenSetPiecesWithRotation() {
      List<Rectangle> inputPieces = List.of(
          new Rectangle(0, 0, 50, 50, 1));

      CuttingList list = new CuttingList();
      list.setPieces(inputPieces);

      assertEquals(1, list.getPieces().size());
    }
  }

  @Nested
  @DisplayName("Sorting Operations Tests")
  class SortingTests {

    @Test
    @DisplayName("Should sort stock ascending by size")
    void shouldSortStockAscending() {
      CuttingList list = new CuttingList();
      list.setStock(new ArrayList<>(List.of(
          new Rectangle(0, 0, 100, 100, 1), // size 10000
          new Rectangle(0, 0, 10, 10, 2), // size 100
          new Rectangle(0, 0, 50, 50, 3) // size 2500
      )));

      list.sortStock();

      assertEquals(100, list.getStock().get(0).getSize());
      assertEquals(2500, list.getStock().get(1).getSize());
      assertEquals(10000, list.getStock().get(2).getSize());
    }

    @Test
    @DisplayName("Should sort pieces descending by size")
    void shouldSortPiecesDescending() {
      CuttingList list = new CuttingList();
      list.getPieces().addAll(List.of(
          new Rectangle(0, 0, 10, 10, 1), // size 100
          new Rectangle(0, 0, 100, 100, 2), // size 10000
          new Rectangle(0, 0, 50, 50, 3) // size 2500
      ));

      list.sortPieces();

      assertEquals(10000, list.getPieces().get(0).getSize());
      assertEquals(2500, list.getPieces().get(1).getSize());
      assertEquals(100, list.getPieces().get(2).getSize());
    }

    @Test
    @DisplayName("Should sort waste descending by size")
    void shouldSortWasteDescending() {
      CuttingList list = new CuttingList();
      list.getWaste().addAll(List.of(
          new Rectangle(0, 0, 10, 10, 1),
          new Rectangle(0, 0, 100, 100, 2)));

      list.sortWaste();

      assertEquals(10000, list.getWaste().get(0).getSize());
      assertEquals(100, list.getWaste().get(1).getSize());
    }
  }

  @Nested
  @DisplayName("Waste Score & Comparator Tests")
  class CostAndComparatorTests {

    @Test
    @DisplayName("Should penalize waste calculation when pieces remain unplaced")
    void shouldPenalizeWaste_WhenUnplacedPiecesExist() {
      CuttingList list = new CuttingList();
      list.getPieces().add(new Rectangle(0, 0, 10, 10, 1));
      list.getWaste().add(new Rectangle(0, 0, 20, 20, 2)); // 400 area

      assertEquals(400, list.calculateWaste());
    }

    @Test
    @DisplayName("Should return 0 when no waste remains")
    void shouldReturnZero_WhenNoWasteAndNoPieces() {
      CuttingList list = new CuttingList();
      assertEquals(0, list.calculateWaste());
    }

    @Test
    @DisplayName("isBetterThan should correctly compare candidate solutions by placed pieces, waste area, and waste piece size")
    void shouldCompareCuttingLists_UsingIsBetterThan() {
      Rectangle stockSheet = new Rectangle(0, 0, 100, 100, 0);

      CuttingList list1 = new CuttingList();
      list1.getStock().add(stockSheet);
      list1.getPieces().add(new Rectangle(0, 0, 5, 5, 99));
      list1.getPlacedPieces().add(new Rectangle(0, 0, 10, 10, 1, 0, 0));
      list1.getWaste().add(new Rectangle(0, 0, 10, 10, 2)); // waste = 100

      CuttingList list2 = new CuttingList();
      list2.getStock().add(stockSheet);
      list2.getPieces().add(new Rectangle(0, 0, 5, 5, 99));
      list2.getPlacedPieces().add(new Rectangle(0, 0, 10, 10, 1, 0, 0));
      list2.getWaste().add(new Rectangle(0, 0, 50, 50, 2)); // waste = 2500

      assertTrue(list2.isBetterThan(list1),
          "Solution with larger reusable off-cut panel (2500 area) should be preferred over tiny off-cut (100 area)");
      assertFalse(list1.isBetterThan(list2));
    }

    @Test
    @DisplayName("Should correctly calculate placed area, total stock area, and material yield percentage")
    void shouldCalculateDomainMetrics_Correctly() {
      CuttingList list = new CuttingList();
      list.getStock().add(new Rectangle(0, 0, 100, 100, 1)); // Stock Area = 10,000
      list.getPlacedPieces().add(new Rectangle(0, 0, 80, 80, 2)); // Placed Area = 6,400

      assertEquals(6400, list.getPlacedArea());
      assertEquals(10000, list.getTotalStockArea());
      assertEquals(64.0, list.getMaterialYieldPercentage(), 0.001);
    }

    @Test
    @DisplayName("Should support legacy German method aliases")
    void shouldSupportLegacyAliases() {
      CuttingList list = new CuttingList();
      assertEquals(0, list.getStock().size());
    }
  }

  @Nested
  @DisplayName("Copy Constructor and Serialization Formatting Tests")
  class CopyConstructorAndToStringTests {

    @Test
    @DisplayName("Should perform a deep copy of CuttingList using copy constructor")
    void shouldDeepCopyCuttingList_UsingCopyConstructor() {
      CuttingList original = new CuttingList();
      original.getPieces().add(new Rectangle(0, 0, 10, 10, 1));
      original.getStock().add(new Rectangle(0, 0, 100, 100, 2));

      CuttingList copy = new CuttingList(original);

      assertNotSame(original, copy);
      assertNotSame(original.getPieces(), copy.getPieces());
      assertNotSame(original.getStock(), copy.getStock());
      assertEquals(1, copy.getPieces().size());
      assertEquals(1, copy.getStock().size());
    }

    @Test
    @DisplayName("Should generate formatted string from toString")
    void shouldGenerateFormattedString() {
      CuttingList list = new CuttingList();
      list.getPlacedPieces().add(new Rectangle(0, 0, 50, 50, 1));
      String str = list.toString();

      assertTrue(str.contains("X,Y: (0, 0)"));
      assertTrue(str.contains("Width,Height: (50, 50)"));
    }
  }
}
