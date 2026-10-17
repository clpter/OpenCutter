package com.cuttingAPI.domain.model;

import com.cuttingAPI.domain.service.CuttingListEvaluator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Pure Java domain aggregate for 2D Bin Packing calculations.
 * Encapsulates piece and stock collections, placement state management,
 * domain calculations (yield, waste area, max off-cut size), multi-criterion
 * sorting,
 * and solution fitness comparison.
 */
public class CuttingList {

  /** Unplaced candidate piece rectangles remaining to be packed. */
  private List<Rectangle> pieces;

  /** Successfully placed piece positions on stock sheets. */
  private List<Rectangle> placedPieces;

  /** Available stock sheets / raw wood panels. */
  private List<Rectangle> stock;

  /** Calculated guillotine cut line sequences (optional). */
  private List<Rectangle> cuts;

  /** Leftover waste off-cut panels remaining on stock sheets. */
  private List<Rectangle> waste;

  /**
   * Constructs a new CuttingList aggregate with candidate pieces and stock
   * sheets.
   *
   * @param pieces initial candidate piece list
   * @param stock  initial stock sheet list
   */
  public CuttingList(List<Rectangle> pieces, List<Rectangle> stock) {
    this.pieces = new ArrayList<>();
    this.stock = new ArrayList<>();
    setPieces(pieces);
    setStock(stock);
    this.placedPieces = new ArrayList<>();
    this.cuts = new ArrayList<>();
    this.waste = new ArrayList<>();
  }

  /** Default no-arg constructor initializing empty collections. */
  public CuttingList() {
    this.cuts = new ArrayList<>();
    this.stock = new ArrayList<>();
    this.pieces = new ArrayList<>();
    this.placedPieces = new ArrayList<>();
    this.waste = new ArrayList<>();
  }

  /**
   * Deep Copy Constructor replacing clone() to preserve encapsulation.
   *
   * @param other template CuttingList to copy
   */
  public CuttingList(CuttingList other) {
    this.cuts = new ArrayList<>();
    this.stock = new ArrayList<>();
    this.pieces = new ArrayList<>();
    this.placedPieces = new ArrayList<>();
    this.waste = new ArrayList<>();
    if (other != null) {
      setPieces(other.pieces);
      setStock(other.stock);
      setCuts(other.cuts);
      setPlacedPieces(other.placedPieces);
      setWaste(other.waste);
    }
  }

  @JsonProperty("pieces")
  public List<Rectangle> getPieces() {
    return pieces;
  }

  @JsonProperty("stock")
  public List<Rectangle> getStock() {
    return stock;
  }

  public List<Rectangle> getCuts() {
    return cuts;
  }

  @JsonProperty("placedPieces")
  public List<Rectangle> getPlacedPieces() {
    return placedPieces;
  }

  @JsonProperty("waste")
  public List<Rectangle> getWaste() {
    return waste;
  }

  /**
   * Helper method for creating defensive copies of rectangle lists.
   *
   * @param source source rectangle list
   * @return new list containing copied rectangles
   */
  private List<Rectangle> copyList(List<Rectangle> source) {
    if (source == null) {
      return new ArrayList<>();
    }
    List<Rectangle> copies = new ArrayList<>(source.size());
    for (Rectangle r : source) {
      copies.add(new Rectangle(r));
    }
    return copies;
  }

  public void setPieces(List<Rectangle> pieces) {
    this.pieces = copyList(pieces);
  }

  public void setPiecesWithoutRotation(List<Rectangle> pieces) {
    setPieces(pieces);
  }

  /**
   * Expands candidate piece list with both standard ($W \times H$) and $90^\circ$
   * rotated ($H \times W$) candidates.
   * Both orientations share the same piece ID so they can be removed together
   * upon placement.
   *
   * @param pieces candidate piece list
   */
  public void setPiecesWithRotation(List<Rectangle> pieces) {
    this.pieces = new ArrayList<>();
    if (pieces != null) {
      for (Rectangle item : pieces) {
        this.pieces.add(new Rectangle(item));
        if (item.getHeight() != item.getWidth()) {
          this.pieces.add(new Rectangle(item, 0, 0, item.getHeight(), item.getWidth()));
        }
      }
    }
  }

  public void setStock(List<Rectangle> stock) {
    this.stock = copyList(stock);
  }

  public void setCuts(List<Rectangle> cuts) {
    this.cuts = copyList(cuts);
  }

  public void setPlacedPieces(List<Rectangle> placedPieces) {
    this.placedPieces = copyList(placedPieces);
  }

  public void setWaste(List<Rectangle> waste) {
    this.waste = copyList(waste);
  }

  /**
   * Domain State Helper: Adds a placed piece position to placed_pieces
   * and automatically removes all remaining candidate orientations matching its
   * piece ID.
   *
   * @param placedPiece placed piece position
   */
  public void addPlacedPiece(Rectangle placedPiece) {
    if (placedPiece != null) {
      this.placedPieces.add(placedPiece);
      removePiece(placedPiece);
    }
  }

  /**
   * Domain State Helper: Tags stock assignment ID onto a waste off-cut rectangle
   * and appends to waste collection.
   *
   * @param wasteRect waste off-cut panel
   * @param stockId   stock sheet assignment ID
   */
  public void addWastePiece(Rectangle wasteRect, int stockId) {
    if (wasteRect != null) {
      wasteRect.setAssignment(stockId);
      this.waste.add(wasteRect);
    }
  }

  /**
   * Removes all candidate piece rectangles matching the target rectangle's ID.
   *
   * @param r target piece to remove by ID
   */
  public void removePiece(Rectangle r) {
    if (r == null || pieces == null) {
      return;
    }
    int id = r.getId();
    Iterator<Rectangle> it = pieces.iterator();
    while (it.hasNext()) {
      if (it.next().getId() == id) {
        it.remove();
      }
    }
  }

  /**
   * Calculates total placed piece surface area.
   *
   * @return total placed area
   */
  public int getPlacedArea() {
    return CuttingListEvaluator.getPlacedArea(this);
  }

  /**
   * Calculates total stock sheet surface area across all available stock sheets.
   *
   * @return total stock area
   */
  public int getTotalStockArea() {
    return CuttingListEvaluator.getTotalStockArea(this);
  }

  /**
   * Calculates total stock sheet surface area of ONLY the stock sheets actually
   * used.
   *
   * @return total used stock area
   */
  public int getUsedStockArea() {
    return CuttingListEvaluator.getUsedStockArea(this);
  }

  /**
   * Calculates the number of raw stock sheets actually used in placement.
   *
   * @return number of used stock sheets
   */
  public int getUsedStockCount() {
    return CuttingListEvaluator.getUsedStockCount(this);
  }

  /**
   * Calculates material fill percentage (placed area / used stock area * 100.0).
   *
   * @return material yield percentage
   */
  public double getMaterialYieldPercentage() {
    return CuttingListEvaluator.getMaterialYieldPercentage(this);
  }

  /**
   * Finds the maximum single waste piece size (area) to prioritize large reusable
   * off-cuts.
   *
   * @return largest single waste piece area
   */
  public int getMaxWastePieceSize() {
    return CuttingListEvaluator.getMaxWastePieceSize(this);
  }

  /**
   * Calculates the Quadratic Waste Concentration Score (sum of waste areas
   * squared in m²).
   *
   * @return quadratic waste score
   */
  public double getQuadraticWasteScore() {
    return CuttingListEvaluator.getQuadraticWasteScore(this);
  }

  /**
   * Filters waste off-cuts that satisfy minimum usable woodworking panel
   * dimensions.
   *
   * @param minDimension minimum width or height threshold in mm (e.g. 100mm)
   * @return list of usable waste panel rectangles
   */
  public List<Rectangle> getUsableWastePieces(int minDimension) {
    return CuttingListEvaluator.getUsableWastePieces(this, minDimension);
  }

  /**
   * Domain Fitness Evaluation: Compares this CuttingList solution against another
   * candidate.
   *
   * @param other candidate solution to compare against
   * @return true if this solution is strictly better than other
   */
  public boolean isBetterThan(CuttingList other) {
    return CuttingListEvaluator.isBetterThan(this, other);
  }

  /**
   * Calculates total residual waste surface area across waste off-cut panels.
   *
   * @return total waste area
   */
  public int calculateWaste() {
    return CuttingListEvaluator.calculateWaste(this);
  }

  /** Sorts stock list by size ascending. */
  public void sortStock() {
    stock.sort(Comparator.comparingInt(Rectangle::getSize));
  }

  /** Sorts candidate pieces by area descending (default Comparable). */
  public void sortPieces() {
    Collections.sort(pieces);
  }

  /** Sorts candidate pieces by area descending. */
  public void sortPiecesByArea() {
    pieces.sort((o1, o2) -> o1.compareTo(o2));
  }

  /** Sorts candidate pieces by longest side dimension descending. */
  public void sortPiecesByLongestSide() {
    pieces.sort(
        (o1, o2) -> Integer.compare(Math.max(o2.getWidth(), o2.getHeight()), Math.max(o1.getWidth(), o1.getHeight())));
  }

  /** Sorts candidate pieces by perimeter descending. */
  public void sortPiecesByPerimeter() {
    pieces
        .sort((o1, o2) -> Integer.compare(2 * (o2.getWidth() + o2.getHeight()), 2 * (o1.getWidth() + o1.getHeight())));
  }

  /** Sorts candidate pieces by width dimension descending. */
  public void sortPiecesByWidth() {
    pieces.sort((o1, o2) -> Integer.compare(o2.getWidth(), o1.getWidth()));
  }

  /** Sorts candidate pieces by height dimension descending. */
  public void sortPiecesByHeight() {
    pieces.sort((o1, o2) -> Integer.compare(o2.getHeight(), o1.getHeight()));
  }

  /**
   * Sorts candidate pieces grouped by width dimension (matching widths clustered
   * together),
   * then by height descending.
   */
  public void sortPiecesByGroupedWidth() {
    pieces.sort((o1, o2) -> {
      int wComp = Integer.compare(o2.getWidth(), o1.getWidth());
      if (wComp != 0) {
        return wComp;
      }
      return Integer.compare(o2.getHeight(), o1.getHeight());
    });
  }

  /**
   * Sorts candidate pieces grouped by height dimension (matching heights
   * clustered together),
   * then by width descending.
   */
  public void sortPiecesByGroupedHeight() {
    pieces.sort((o1, o2) -> {
      int hComp = Integer.compare(o2.getHeight(), o1.getHeight());
      if (hComp != 0) {
        return hComp;
      }
      return Integer.compare(o2.getWidth(), o1.getWidth());
    });
  }

  /** Sorts waste off-cuts by size descending. */
  public void sortWaste() {
    Collections.sort(waste);
  }

  @Override
  public String toString() {
    int totalWaste = calculateWaste();
    StringBuilder sb = new StringBuilder();
    for (Rectangle item : placedPieces) {
      sb.append(String.format("X,Y: (%s, %s)\tWidth,Height: (%s, %s)\n", item.getX(), item.getY(),
          item.getWidth(), item.getHeight()));
    }
    sb.append("\n Waste: ").append(totalWaste);
    return sb.toString();
  }
}
