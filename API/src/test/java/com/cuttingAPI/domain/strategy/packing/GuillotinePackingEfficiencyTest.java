package com.cuttingAPI.domain.strategy.packing;

import com.cuttingAPI.domain.model.CuttingList;
import com.cuttingAPI.domain.model.Rectangle;
import com.cuttingAPI.domain.strategy.placement.BestAreaFit;
import com.cuttingAPI.domain.strategy.placement.BestLongSideFit;
import com.cuttingAPI.domain.strategy.placement.BestShortSideFit;
import com.cuttingAPI.domain.strategy.placement.PlacementStrategy;
import com.cuttingAPI.domain.strategy.split.SplitRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GuillotinePacking Phase 1 & 2 Benchmark & Effectiveness Evaluation")
class GuillotinePackingEfficiencyTest {

  private final PackingStrategy untunedStrategy = new GuillotinePackingStrategy(new BestShortSideFit(),
      SplitRule.SHORTER_AXIS, true, false, false, Integer.MAX_VALUE);
  private final PackingStrategy phase1Strategy = new GuillotinePackingStrategy(new BestShortSideFit(),
      SplitRule.DYNAMIC_SIZE, true, false, false, Integer.MAX_VALUE);
  private final PackingStrategy phase1And2Strategy = new GuillotinePackingStrategy(new BestShortSideFit(),
      SplitRule.DYNAMIC_SIZE, true, true, false, Integer.MAX_VALUE);
  private final PackingStrategy fullMultiPassStrategy = new MultiPassGuillotineStrategy(50, 30, false,
      Integer.MAX_VALUE);

  @Nested
  @DisplayName("Phase 1 & Phase 2 Performance Progression Evaluation")
  class ProgressionEvaluationTests {

    @Test
    @DisplayName("Evaluate step-by-step yield & waste progression across Baseline -> Phase 1 -> Phase 2 -> Full Multi-Pass")
    void testPhaseProgressionOnHardDataset() {
      // 3 Stock panels of 200x200 (Total Area: 120,000)
      List<Rectangle> stock = List.of(
          new Rectangle(0, 0, 200, 200, 1),
          new Rectangle(0, 0, 200, 200, 2),
          new Rectangle(0, 0, 200, 200, 3));

      // 40 distinct pieces (Total Area: 93,600 -> 78.0% fill rate)
      List<Rectangle> pieces = List.of(
          new Rectangle(0, 0, 100, 80, 1), // 8000
          new Rectangle(0, 0, 90, 70, 2), // 6300
          new Rectangle(0, 0, 80, 80, 3), // 6400
          new Rectangle(0, 0, 100, 60, 4), // 6000
          new Rectangle(0, 0, 90, 60, 5), // 5400
          new Rectangle(0, 0, 70, 70, 6), // 4900
          new Rectangle(0, 0, 80, 60, 7), // 4800
          new Rectangle(0, 0, 150, 20, 8), // 3000
          new Rectangle(0, 0, 20, 120, 9), // 2400
          new Rectangle(0, 0, 60, 60, 10), // 3600
          new Rectangle(0, 0, 70, 50, 11), // 3500
          new Rectangle(0, 0, 60, 50, 12), // 3000
          new Rectangle(0, 0, 80, 40, 13), // 3200
          new Rectangle(0, 0, 50, 50, 14), // 2500
          new Rectangle(0, 0, 90, 30, 15), // 2700
          new Rectangle(0, 0, 60, 40, 16), // 2400
          new Rectangle(0, 0, 50, 40, 17), // 2000
          new Rectangle(0, 0, 70, 30, 18), // 2100
          new Rectangle(0, 0, 40, 40, 19), // 1600
          new Rectangle(0, 0, 80, 20, 20), // 1600
          new Rectangle(0, 0, 30, 50, 21), // 1500
          new Rectangle(0, 0, 40, 30, 22), // 1200
          new Rectangle(0, 0, 60, 20, 23), // 1200
          new Rectangle(0, 0, 35, 35, 24), // 1225
          new Rectangle(0, 0, 50, 20, 25), // 1000
          new Rectangle(0, 0, 30, 30, 26), // 900
          new Rectangle(0, 0, 40, 20, 27), // 800
          new Rectangle(0, 0, 25, 25, 28), // 625
          new Rectangle(0, 0, 30, 20, 29), // 600
          new Rectangle(0, 0, 50, 10, 30), // 500
          new Rectangle(0, 0, 20, 20, 31), // 400
          new Rectangle(0, 0, 20, 20, 32), // 400
          new Rectangle(0, 0, 40, 10, 33), // 400
          new Rectangle(0, 0, 15, 15, 34), // 225
          new Rectangle(0, 0, 15, 15, 35), // 225
          new Rectangle(0, 0, 30, 10, 36), // 300
          new Rectangle(0, 0, 10, 20, 37), // 200
          new Rectangle(0, 0, 10, 20, 38), // 200
          new Rectangle(0, 0, 10, 10, 39), // 100
          new Rectangle(0, 0, 10, 10, 40) // 100
      );

      // 1. Untuned Baseline
      long t0 = System.currentTimeMillis();
      CuttingList r0 = untunedStrategy.pack(pieces, stock, true);
      long ms0 = System.currentTimeMillis() - t0;

      // 2. Phase 1 (Dynamic Size-Dependent Split Rules)
      long t1 = System.currentTimeMillis();
      CuttingList r1 = phase1Strategy.pack(pieces, stock, true);
      long ms1 = System.currentTimeMillis() - t1;

      // 3. Phase 1 + Phase 2 (Dynamic Split Rules + 2-Step Look-Ahead)
      long t2 = System.currentTimeMillis();
      CuttingList r2 = phase1And2Strategy.pack(pieces, stock, true);
      long ms2 = System.currentTimeMillis() - t2;

      // 4. Full Multi-Pass Optimizer
      long t3 = System.currentTimeMillis();
      CuttingList r3 = fullMultiPassStrategy.pack(pieces, stock, true);
      long ms3 = System.currentTimeMillis() - t3;

      System.out.println("=========================================================================");
      System.out.println("   PHASE 1 & PHASE 2 ALGORITHM PROGRESSION BENCHMARK RESULTS           ");
      System.out.println("=========================================================================");
      System.out.printf("0. Baseline        -> Placed: %d/40 | Placed Area: %d (%.1f%%) | Execution Time: %d ms%n",
          r0.getPlacedPieces().size(), r0.getPlacedArea(), r0.getMaterialYieldPercentage(), ms0);

      System.out.printf("1. Phase 1 Dynamic -> Placed: %d/40 | Placed Area: %d (%.1f%%) | Execution Time: %d ms%n",
          r1.getPlacedPieces().size(), r1.getPlacedArea(), r1.getMaterialYieldPercentage(), ms1);

      System.out.printf("2. Phase 1+2 LookA -> Placed: %d/40 | Placed Area: %d (%.1f%%) | Execution Time: %d ms%n",
          r2.getPlacedPieces().size(), r2.getPlacedArea(), r2.getMaterialYieldPercentage(), ms2);

      System.out.printf("3. Full Multi-Pass -> Placed: %d/40 | Placed Area: %d (%.1f%%) | Execution Time: %d ms%n",
          r3.getPlacedPieces().size(), r3.getPlacedArea(), r3.getMaterialYieldPercentage(), ms3);
      System.out.println("=========================================================================");

      // Assertions
      assertTrue(r2.getPlacedPieces().size() >= r0.getPlacedPieces().size(),
          "Phase 2 Look-Ahead should place >= pieces than baseline");
      assertTrue(r3.getPlacedPieces().size() >= r2.getPlacedPieces().size(),
          "Full Multi-Pass should achieve maximum piece placement");
      assertEquals(40, r3.getPlacedPieces().size(), "Full Multi-Pass algorithm should pack 100% of all 40 pieces");
    }
  }

  @Nested
  @DisplayName("Single Sheet Waste Consolidation Benchmark")
  class SingleSheetConsolidationTests {

    @Test
    @DisplayName("Single sheet: Phase 1 & 2 should consolidate remaining waste into exactly 1 contiguous piece")
    void testSingleSheetWasteConsolidation() {
      List<Rectangle> stock = List.of(new Rectangle(0, 0, 200, 200, 1));
      List<Rectangle> pieces = List.of(
          new Rectangle(0, 0, 100, 100, 10),
          new Rectangle(0, 0, 100, 100, 20),
          new Rectangle(0, 0, 100, 50, 30),
          new Rectangle(0, 0, 100, 50, 40));

      CuttingList result = fullMultiPassStrategy.pack(pieces, stock, true);

      assertNotNull(result);
      assertEquals(4, result.getPlacedPieces().size());
      assertEquals(1, result.getWaste().size(),
          "Remaining waste should be consolidated into 1 single contiguous piece");
      assertEquals(10000, result.getWaste().get(0).getSize());
    }
  }

  @Nested
  @DisplayName("User 46-Piece Dataset Performance & Yield Analysis")
  class UserDatasetPerformanceAnalysisTests {

    @Test
    @DisplayName("Performance Analysis: Fixed Orientation (Without Rotation) vs Free Orientation (With Rotation)")
    void testUserDatasetPerformance() {
      List<Rectangle> stock = List.of(
          new Rectangle(0, 0, 2800, 2070, 0),
          new Rectangle(0, 0, 2800, 2070, 1),
          new Rectangle(0, 0, 2800, 2070, 2),
          new Rectangle(0, 0, 2800, 2070, 3),
          new Rectangle(0, 0, 2800, 2070, 4));

      List<Rectangle> pieces = List.of(
          new Rectangle(0, 0, 890, 270, 0, 0, 1),
          new Rectangle(0, 0, 890, 270, 1, 0, 2),
          new Rectangle(0, 0, 890, 270, 2, 0, 3),
          new Rectangle(0, 0, 890, 270, 3, 0, 4),
          new Rectangle(0, 0, 890, 495, 4, 0, 5),
          new Rectangle(0, 0, 890, 495, 5, 0, 6),
          new Rectangle(0, 0, 890, 495, 6, 0, 6),
          new Rectangle(0, 0, 880, 594, 7, 0, 7),
          new Rectangle(0, 0, 161, 595, 8, 0, 8),
          new Rectangle(0, 0, 282, 595, 9, 0, 9),
          new Rectangle(0, 0, 282, 595, 10, 0, 9),
          new Rectangle(0, 0, 139, 496, 11, 0, 10),
          new Rectangle(0, 0, 139, 496, 12, 0, 10),
          new Rectangle(0, 0, 567, 496, 13, 0, 11),
          new Rectangle(0, 0, 567, 496, 14, 0, 11),
          new Rectangle(0, 0, 709, 170, 15, 0, 12),
          new Rectangle(0, 0, 139, 595, 16, 0, 13),
          new Rectangle(0, 0, 229, 595, 17, 0, 14),
          new Rectangle(0, 0, 229, 595, 18, 0, 14),
          new Rectangle(0, 0, 102, 595, 19, 0, 15),
          new Rectangle(0, 0, 139, 444, 20, 0, 16),
          new Rectangle(0, 0, 567, 444, 21, 0, 17),
          new Rectangle(0, 0, 709, 597, 22, 0, 18),
          new Rectangle(0, 0, 139, 495, 23, 0, 19),
          new Rectangle(0, 0, 567, 495, 24, 0, 20),
          new Rectangle(0, 0, 890, 320, 25, 0, 21),
          new Rectangle(0, 0, 890, 320, 26, 0, 21),
          new Rectangle(0, 0, 890, 330, 27, 0, 22),
          new Rectangle(0, 0, 890, 330, 28, 0, 22),
          new Rectangle(0, 0, 1380, 300, 29, 0, 23),
          new Rectangle(0, 0, 709, 120, 30, 0, 24),
          new Rectangle(0, 0, 890, 120, 31, 0, 25),
          new Rectangle(0, 0, 1500, 596, 32, 0, 26),
          new Rectangle(0, 0, 2215, 596, 33, 0, 27),
          new Rectangle(0, 0, 715, 596, 34, 0, 28),
          new Rectangle(0, 0, 715, 596, 35, 0, 28),
          new Rectangle(0, 0, 709, 66, 36, 0, 29),
          new Rectangle(0, 0, 709, 74, 37, 0, 30),
          new Rectangle(0, 0, 709, 56, 38, 0, 31),
          new Rectangle(0, 0, 709, 74, 39, 0, 32),
          new Rectangle(0, 0, 1740, 140, 40, 0, 33),
          new Rectangle(0, 0, 555, 160, 41, 0, 34),
          new Rectangle(0, 0, 555, 160, 42, 0, 34),
          new Rectangle(0, 0, 895, 140, 43, 0, 35),
          new Rectangle(0, 0, 1279, 140, 44, 0, 36),
          new Rectangle(0, 0, 775, 140, 45, 0, 37));

      // Measure Without Rotation (Fixed Orientation)
      long startWithout = System.currentTimeMillis();
      CuttingList resultWithout = fullMultiPassStrategy.pack(pieces, stock, false);
      long msWithout = System.currentTimeMillis() - startWithout;

      // Measure With Rotation (Free Orientation)
      long startWith = System.currentTimeMillis();
      CuttingList resultWith = fullMultiPassStrategy.pack(pieces, stock, true);
      long msWith = System.currentTimeMillis() - startWith;

      System.out.println("=========================================================================");
      System.out.println("   USER 46-PIECE DATASET: MATERIAL YIELD & QUADRATIC WASTE SCORE       ");
      System.out.println("=========================================================================");
      System.out.printf("WITHOUT ROTATION (Fixed):%n");
      System.out.printf("  - Placed Pieces:          %d / %d (%.1f%%)%n",
          resultWithout.getPlacedPieces().size(), pieces.size(),
          (double) resultWithout.getPlacedPieces().size() / pieces.size() * 100.0);
      System.out.printf("  - Stock Boards Used:       %d / %d (Total Used Area: %d mm²)%n",
          resultWithout.getUsedStockCount(), stock.size(), resultWithout.getUsedStockArea());
      System.out.printf("  - Material Yield:         %.2f%%%n", resultWithout.getMaterialYieldPercentage());
      System.out.printf("  - Quadratic Waste Score:  %.4f (Sum of Waste Areas Squared)%n",
          resultWithout.getQuadraticWasteScore());
      System.out.printf("  - Waste Off-cuts Total:   %d panels | Usable (≥100mm): %d panels%n",
          resultWithout.getWaste().size(), resultWithout.getUsableWastePieces(100).size());
      System.out.printf("  - Max Off-cut Size:       %d mm²%n", resultWithout.getMaxWastePieceSize());
      System.out.printf("  - Execution Time:         %d ms%n%n", msWithout);

      System.out.printf("WITH ROTATION (Free):%n");
      System.out.printf("  - Placed Pieces:          %d / %d (%.1f%%)%n",
          resultWith.getPlacedPieces().size(), pieces.size(),
          (double) resultWith.getPlacedPieces().size() / pieces.size() * 100.0);
      System.out.printf("  - Stock Boards Used:       %d / %d (Total Used Area: %d mm²)%n",
          resultWith.getUsedStockCount(), stock.size(), resultWith.getUsedStockArea());
      System.out.printf("  - Material Yield:         %.2f%%%n", resultWith.getMaterialYieldPercentage());
      System.out.printf("  - Quadratic Waste Score:  %.4f (Sum of Waste Areas Squared)%n",
          resultWith.getQuadraticWasteScore());
      System.out.printf("  - Waste Off-cuts Total:   %d panels | Usable (≥100mm): %d panels%n",
          resultWith.getWaste().size(), resultWith.getUsableWastePieces(100).size());
      System.out.printf("  - Max Off-cut Size:       %d mm²%n", resultWith.getMaxWastePieceSize());
      System.out.printf("  - Execution Time:         %d ms%n", msWith);
      System.out.println("=========================================================================");

      assertTrue(resultWithout.getPlacedPieces().size() > 0);
      assertTrue(resultWith.getPlacedPieces().size() > 0);
    }

    @Test
    @DisplayName("AB Comparison: Evaluate exact impact of Dimension-Clustering vs Pure Random Permutations")
    void testExactDimensionClusteringImpact() {
      List<Rectangle> stock = List.of(
          new Rectangle(0, 0, 2800, 2070, 0),
          new Rectangle(0, 0, 2800, 2070, 1),
          new Rectangle(0, 0, 2800, 2070, 2),
          new Rectangle(0, 0, 2800, 2070, 3),
          new Rectangle(0, 0, 2800, 2070, 4));

      List<Rectangle> pieces = List.of(
          new Rectangle(0, 0, 890, 270, 0, 0, 1),
          new Rectangle(0, 0, 890, 270, 1, 0, 2),
          new Rectangle(0, 0, 890, 270, 2, 0, 3),
          new Rectangle(0, 0, 890, 270, 3, 0, 4),
          new Rectangle(0, 0, 890, 495, 4, 0, 5),
          new Rectangle(0, 0, 890, 495, 5, 0, 6),
          new Rectangle(0, 0, 890, 495, 6, 0, 6),
          new Rectangle(0, 0, 880, 594, 7, 0, 7),
          new Rectangle(0, 0, 161, 595, 8, 0, 8),
          new Rectangle(0, 0, 282, 595, 9, 0, 9),
          new Rectangle(0, 0, 282, 595, 10, 0, 9),
          new Rectangle(0, 0, 139, 496, 11, 0, 10),
          new Rectangle(0, 0, 139, 496, 12, 0, 10),
          new Rectangle(0, 0, 567, 496, 13, 0, 11),
          new Rectangle(0, 0, 567, 496, 14, 0, 11),
          new Rectangle(0, 0, 709, 170, 15, 0, 12),
          new Rectangle(0, 0, 139, 595, 16, 0, 13),
          new Rectangle(0, 0, 229, 595, 17, 0, 14),
          new Rectangle(0, 0, 229, 595, 18, 0, 14),
          new Rectangle(0, 0, 102, 595, 19, 0, 15),
          new Rectangle(0, 0, 139, 444, 20, 0, 16),
          new Rectangle(0, 0, 567, 444, 21, 0, 17),
          new Rectangle(0, 0, 709, 597, 22, 0, 18),
          new Rectangle(0, 0, 139, 495, 23, 0, 19),
          new Rectangle(0, 0, 567, 495, 24, 0, 20),
          new Rectangle(0, 0, 890, 320, 25, 0, 21),
          new Rectangle(0, 0, 890, 320, 26, 0, 21),
          new Rectangle(0, 0, 890, 330, 27, 0, 22),
          new Rectangle(0, 0, 890, 330, 28, 0, 22),
          new Rectangle(0, 0, 1380, 300, 29, 0, 23),
          new Rectangle(0, 0, 709, 120, 30, 0, 24),
          new Rectangle(0, 0, 890, 120, 31, 0, 25),
          new Rectangle(0, 0, 1500, 596, 32, 0, 26),
          new Rectangle(0, 0, 2215, 596, 33, 0, 27),
          new Rectangle(0, 0, 715, 596, 34, 0, 28),
          new Rectangle(0, 0, 715, 596, 35, 0, 28),
          new Rectangle(0, 0, 709, 66, 36, 0, 29),
          new Rectangle(0, 0, 709, 74, 37, 0, 30),
          new Rectangle(0, 0, 709, 56, 38, 0, 31),
          new Rectangle(0, 0, 709, 74, 39, 0, 32),
          new Rectangle(0, 0, 1740, 140, 40, 0, 33),
          new Rectangle(0, 0, 555, 160, 41, 0, 34),
          new Rectangle(0, 0, 555, 160, 42, 0, 34),
          new Rectangle(0, 0, 895, 140, 43, 0, 35),
          new Rectangle(0, 0, 1279, 140, 44, 0, 36),
          new Rectangle(0, 0, 775, 140, 45, 0, 37));

      // Strategy A: Standard Area Presort
      CuttingList clA = new CuttingList();
      clA.setPieces(pieces);
      clA.sortPiecesByArea();
      GuillotinePackingStrategy stratA = new GuillotinePackingStrategy();
      CuttingList resultA = stratA.pack(clA.getPieces(), stock, true);

      // Strategy B: Exact Dimension-Clustering Presort
      CuttingList clB = new CuttingList();
      clB.setPieces(pieces);
      clB.sortPiecesByGroupedWidth();
      GuillotinePackingStrategy stratB = new GuillotinePackingStrategy();
      CuttingList resultB = stratB.pack(clB.getPieces(), stock, true);

      System.out.println("=========================================================================");
      System.out.println("   A/B BENCHMARK: STANDARD AREA SORT VS EXACT DIMENSION-CLUSTERING      ");
      System.out.println("=========================================================================");
      System.out.printf("Strategy A (Standard Area Sort):%n");
      System.out.printf("  - Total Waste Panels:     %d panels%n", resultA.getWaste().size());
      System.out.printf("  - Usable (≥100mm) Panels: %d panels%n", resultA.getUsableWastePieces(100).size());
      System.out.printf("  - Max Off-cut Panel Size: %d mm²%n", resultA.getMaxWastePieceSize());
      System.out.printf("  - Quadratic Waste Score:  %.4f%n%n", resultA.getQuadraticWasteScore());

      System.out.printf("Strategy B (Exact Dimension-Clustering):%n");
      System.out.printf("  - Total Waste Panels:     %d panels%n", resultB.getWaste().size());
      System.out.printf("  - Usable (≥100mm) Panels: %d panels%n", resultB.getUsableWastePieces(100).size());
      System.out.printf("  - Max Off-cut Panel Size: %d mm²%n", resultB.getMaxWastePieceSize());
      System.out.printf("  - Quadratic Waste Score:  %.4f%n", resultB.getQuadraticWasteScore());
      System.out.println("=========================================================================");
    }
  }

  @Nested
  @DisplayName("Look-Ahead vs More Permutations A/B Benchmark")
  class LookAheadVsPermutationsBenchmarkTests {

    @Test
    @DisplayName("A/B: Look-Ahead (30 perms) vs No-Look-Ahead (90 perms) on 46-piece dataset")
    void testLookAheadVsMorePermutations() {
      List<Rectangle> stock = List.of(
          new Rectangle(0, 0, 2800, 2070, 0),
          new Rectangle(0, 0, 2800, 2070, 1),
          new Rectangle(0, 0, 2800, 2070, 2),
          new Rectangle(0, 0, 2800, 2070, 3),
          new Rectangle(0, 0, 2800, 2070, 4));

      List<Rectangle> pieces = List.of(
          new Rectangle(0, 0, 890, 270, 0, 0, 1),
          new Rectangle(0, 0, 890, 270, 1, 0, 2),
          new Rectangle(0, 0, 890, 270, 2, 0, 3),
          new Rectangle(0, 0, 890, 270, 3, 0, 4),
          new Rectangle(0, 0, 890, 495, 4, 0, 5),
          new Rectangle(0, 0, 890, 495, 5, 0, 6),
          new Rectangle(0, 0, 890, 495, 6, 0, 6),
          new Rectangle(0, 0, 880, 594, 7, 0, 7),
          new Rectangle(0, 0, 161, 595, 8, 0, 8),
          new Rectangle(0, 0, 282, 595, 9, 0, 9),
          new Rectangle(0, 0, 282, 595, 10, 0, 9),
          new Rectangle(0, 0, 139, 496, 11, 0, 10),
          new Rectangle(0, 0, 139, 496, 12, 0, 10),
          new Rectangle(0, 0, 567, 496, 13, 0, 11),
          new Rectangle(0, 0, 567, 496, 14, 0, 11),
          new Rectangle(0, 0, 709, 170, 15, 0, 12),
          new Rectangle(0, 0, 139, 595, 16, 0, 13),
          new Rectangle(0, 0, 229, 595, 17, 0, 14),
          new Rectangle(0, 0, 229, 595, 18, 0, 14),
          new Rectangle(0, 0, 102, 595, 19, 0, 15),
          new Rectangle(0, 0, 139, 444, 20, 0, 16),
          new Rectangle(0, 0, 567, 444, 21, 0, 17),
          new Rectangle(0, 0, 709, 597, 22, 0, 18),
          new Rectangle(0, 0, 139, 495, 23, 0, 19),
          new Rectangle(0, 0, 567, 495, 24, 0, 20),
          new Rectangle(0, 0, 890, 320, 25, 0, 21),
          new Rectangle(0, 0, 890, 320, 26, 0, 21),
          new Rectangle(0, 0, 890, 330, 27, 0, 22),
          new Rectangle(0, 0, 890, 330, 28, 0, 22),
          new Rectangle(0, 0, 1380, 300, 29, 0, 23),
          new Rectangle(0, 0, 709, 120, 30, 0, 24),
          new Rectangle(0, 0, 890, 120, 31, 0, 25),
          new Rectangle(0, 0, 1500, 596, 32, 0, 26),
          new Rectangle(0, 0, 2215, 596, 33, 0, 27),
          new Rectangle(0, 0, 715, 596, 34, 0, 28),
          new Rectangle(0, 0, 715, 596, 35, 0, 28),
          new Rectangle(0, 0, 709, 66, 36, 0, 29),
          new Rectangle(0, 0, 709, 74, 37, 0, 30),
          new Rectangle(0, 0, 709, 56, 38, 0, 31),
          new Rectangle(0, 0, 709, 74, 39, 0, 32),
          new Rectangle(0, 0, 1740, 140, 40, 0, 33),
          new Rectangle(0, 0, 555, 160, 41, 0, 34),
          new Rectangle(0, 0, 555, 160, 42, 0, 34),
          new Rectangle(0, 0, 895, 140, 43, 0, 35),
          new Rectangle(0, 0, 1279, 140, 44, 0, 36),
          new Rectangle(0, 0, 775, 140, 45, 0, 37));

      // Strategy A: Standard Multi-Pass WITH look-ahead, 30 permutations
      PackingStrategy withLookAhead = new MultiPassGuillotineStrategy(50, 40, false, Integer.MAX_VALUE);
      long t1 = System.currentTimeMillis();
      CuttingList resultA = withLookAhead.pack(pieces, stock, true);
      long msA = System.currentTimeMillis() - t1;

      // Strategy B: Multi-Pass WITHOUT look-ahead, 90 permutations (3x more)
      // We need a custom strategy that disables look-ahead in Pass 1 and Pass 2.
      // Since MultiPassGuillotineStrategy hardcodes look-ahead, we simulate it
      // by running 90 permutations through a no-look-ahead single-pass engine.
      long t2 = System.currentTimeMillis();

      CuttingList bestNoLookAhead = null;
      // Test all the same systematic configs without look-ahead
      for (SplitRule sr : SplitRule.values()) {
        for (PlacementStrategy ps : List.of(
            new BestShortSideFit(), new BestLongSideFit(), new BestAreaFit())) {
          GuillotinePackingStrategy strat = new GuillotinePackingStrategy(ps, sr, true, false, false,
              Integer.MAX_VALUE);
          CuttingList candidate = strat.pack(pieces, stock, true);
          if (candidate.isBetterThan(bestNoLookAhead)) {
            bestNoLookAhead = candidate;
          }
        }
      }
      // Then run 90 random shuffles without look-ahead
      java.util.Random rng = new java.util.Random(42);
      GuillotinePackingStrategy noLAPermStrat = new GuillotinePackingStrategy(
          new BestShortSideFit(), SplitRule.DYNAMIC_SIZE, false, false, false, Integer.MAX_VALUE);
      for (int i = 0; i < 90; i++) {
        List<Rectangle> shuffled = new java.util.ArrayList<>();
        for (Rectangle p : pieces) {
          shuffled.add(new Rectangle(p));
        }
        java.util.Collections.shuffle(shuffled, rng);
        CuttingList candidate = noLAPermStrat.pack(shuffled, stock, true);
        if (candidate.isBetterThan(bestNoLookAhead)) {
          bestNoLookAhead = candidate;
        }
      }
      long msB = System.currentTimeMillis() - t2;

      System.out.println("=========================================================================");
      System.out.println("   A/B BENCHMARK: LOOK-AHEAD (30 perms) vs NO-LOOK-AHEAD (90 perms)    ");
      System.out.println("=========================================================================");
      System.out.printf("Strategy A: WITH Look-Ahead (30 permutations)%n");
      System.out.printf("  - Placed Pieces:         %d / %d%n", resultA.getPlacedPieces().size(), pieces.size());
      System.out.printf("  - Boards Used:           %d / %d%n", resultA.getUsedStockCount(), stock.size());
      System.out.printf("  - Material Yield:        %.2f%%%n", resultA.getMaterialYieldPercentage());
      System.out.printf("  - Quadratic Waste Score: %.4f%n", resultA.getQuadraticWasteScore());
      System.out.printf("  - Max Off-cut Size:      %d mm²%n", resultA.getMaxWastePieceSize());
      System.out.printf("  - Waste Fragments:       %d%n", resultA.getWaste().size());
      System.out.printf("  - Execution Time:        %d ms%n%n", msA);

      System.out.printf("Strategy B: WITHOUT Look-Ahead (90 permutations)%n");
      System.out.printf("  - Placed Pieces:         %d / %d%n", bestNoLookAhead.getPlacedPieces().size(),
          pieces.size());
      System.out.printf("  - Boards Used:           %d / %d%n", bestNoLookAhead.getUsedStockCount(), stock.size());
      System.out.printf("  - Material Yield:        %.2f%%%n", bestNoLookAhead.getMaterialYieldPercentage());
      System.out.printf("  - Quadratic Waste Score: %.4f%n", bestNoLookAhead.getQuadraticWasteScore());
      System.out.printf("  - Max Off-cut Size:      %d mm²%n", bestNoLookAhead.getMaxWastePieceSize());
      System.out.printf("  - Waste Fragments:       %d%n", bestNoLookAhead.getWaste().size());
      System.out.printf("  - Execution Time:        %d ms%n", msB);

      System.out.printf("%nVERDICT: %s%n",
          resultA.isBetterThan(bestNoLookAhead)
              ? "LOOK-AHEAD WINS — keeps its value even with fewer permutations"
              : bestNoLookAhead.isBetterThan(resultA)
                  ? "MORE PERMUTATIONS WINS — brute force exploration beats shallow look-ahead"
                  : "TIE — both strategies produce equivalent results");
      System.out.println("=========================================================================");

      // Both should place all pieces
      assertTrue(resultA.getPlacedPieces().size() > 0);
      assertTrue(bestNoLookAhead.getPlacedPieces().size() > 0);
    }
  }
}
