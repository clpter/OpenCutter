package com.cuttingAPI.domain.model;

public class FitResult {
  public final Rectangle piece;
  public final Rectangle freeRect;
  public final long fitScore;

  public FitResult(Rectangle piece, Rectangle freeRect, long fitScore) {
    this.piece = piece;
    this.freeRect = freeRect;
    this.fitScore = fitScore;
  }
}
