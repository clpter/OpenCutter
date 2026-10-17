package com.cuttingAPI.domain.service;

import com.cuttingAPI.domain.model.CuttingList;
import com.cuttingAPI.domain.model.Rectangle;
import com.cuttingAPI.domain.strategy.packing.PackingStrategy;

import java.util.List;

/**
 * Pure domain service orchestrating 2D Guillotine cutting optimization
 * calculation tasks.
 * Serves as the primary entry point for domain calculations, wrapping a
 * CuttingList aggregate
 * and delegating packing calculations to an immutable PackingStrategy
 * algorithm.
 */
public class CuttingOptimizer {

  /**
   * Target CuttingList domain aggregate containing pieces, stock, placed
   * positions, and waste.
   */
  private CuttingList cuttingList;

  /** Immutable macro packing strategy algorithm instance. */
  private final PackingStrategy strategy;

  /**
   * Constructs a CuttingOptimizer with raw piece and stock lists using a custom
   * PackingStrategy.
   *
   * @param pieces   candidate piece list
   * @param stock    available stock sheet list
   * @param strategy custom packing strategy instance
   */
  public CuttingOptimizer(List<Rectangle> pieces, List<Rectangle> stock, PackingStrategy strategy) {
    this.cuttingList = new CuttingList(pieces, stock);
    this.strategy = strategy;
  }

  /**
   * Constructs a CuttingOptimizer with an existing CuttingList using a custom
   * PackingStrategy.
   *
   * @param cuttingList source CuttingList domain aggregate
   * @param strategy    custom packing strategy instance
   */
  public CuttingOptimizer(CuttingList cuttingList, PackingStrategy strategy) {
    this.cuttingList = cuttingList != null ? new CuttingList(cuttingList) : new CuttingList();
    this.strategy = strategy;
  }

  public CuttingList getCuttingList() {
    return cuttingList;
  }

  public PackingStrategy getStrategy() {
    return strategy;
  }

  /**
   * Executes 2D Guillotine bin packing without allowing piece rotation (fixed
   * orientation).
   */
  public void cutFixedDirection() {
    this.cuttingList = strategy.pack(cuttingList.getPieces(), cuttingList.getStock(), false);
  }

  /**
   * Executes 2D Guillotine bin packing allowing 90-degree piece rotation.
   */
  public void cutWithRotation() {
    this.cuttingList = strategy.pack(cuttingList.getPieces(), cuttingList.getStock(), true);
  }

  @Override
  public String toString() {
    return cuttingList.toString();
  }
}
