package com.cuttingAPI.application.dto;

import com.cuttingAPI.domain.model.Rectangle;

import java.util.List;

/**
 * Data Transfer Object (DTO) for 2D Bin Packing calculation requests.
 */
public class CuttingRequest {

  private List<Rectangle> stock;
  private List<Rectangle> pieces;
  private boolean preferVerticalCuts = false;
  private int maxCuttingLength = Integer.MAX_VALUE;

  public CuttingRequest() {
  }

  public CuttingRequest(List<Rectangle> pieces, List<Rectangle> stock) {
    this.pieces = pieces;
    this.stock = stock;
  }

  public CuttingRequest(List<Rectangle> stock, List<Rectangle> pieces, boolean prefereVerticalCuts,
      int maxCuttingLength) {
    this.stock = stock;
    this.pieces = pieces;
    this.preferVerticalCuts = prefereVerticalCuts;
    this.maxCuttingLength = maxCuttingLength;
  }

  public List<Rectangle> getStock() {
    return stock;
  }

  public void setStock(List<Rectangle> stock) {
    this.stock = stock;
  }

  public List<Rectangle> getPieces() {
    return pieces;
  }

  public void setPieces(List<Rectangle> pieces) {
    this.pieces = pieces;
  }

  public boolean getPreferVerticalCuts() {
    return preferVerticalCuts;
  }

  public void setPreferVerticalCuts(boolean prefereVerticalCuts) {
    this.preferVerticalCuts = prefereVerticalCuts;
  }

  public int getMaxCuttingLength() {
    return maxCuttingLength;
  }

  public void setMaxCuttingLength(int maxCuttingLength) {
    this.maxCuttingLength = maxCuttingLength;
  }
}
