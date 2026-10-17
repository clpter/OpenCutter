package com.cuttingAPI.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pure Java domain model representing a 2D rectangle shape.
 * Serves as the fundamental building block for stock sheets, cut pieces,
 * and waste off-cut panels in the 2D Guillotine cutting engine.
 * Implements Comparable to enable area-descending sorting.
 */
public class Rectangle implements Comparable<Rectangle> {

  /** Unique identifier for SVG component rendering and piece tracking. */
  private int id;

  /**
   * Stock sheet ID assignment indicating which stock sheet a piece or waste
   * off-cut belongs to.
   */
  private int assignment;

  /** User input row number (Nr) displayed inside rendered board shapes. */
  private int number;

  /** X coordinate of top-left corner on the stock panel. */
  private int x;

  /** Y coordinate of top-left corner on the stock panel. */
  private int y;

  /** Width dimension in units (mm / cm / inches). */
  private int width;

  /** Height dimension in units (mm / cm / inches). */
  private int height;

  // =========================================================================
  // GROUP 1: PRIMITIVE CONSTRUCTORS (Building from raw values)
  // =========================================================================

  /**
   * Master Primitive Constructor initializing all rectangle properties.
   * All primitive constructors delegate to this method.
   *
   * @param x          X coordinate
   * @param y          Y coordinate
   * @param width      width dimension
   * @param height     height dimension
   * @param id         unique rectangle ID
   * @param assignment stock sheet assignment ID
   * @param number     user row number
   */
  public Rectangle(int x, int y, int width, int height, int id, int assignment, int number) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.id = id;
    this.assignment = assignment;
    this.number = number;
  }

  /**
   * Primitive constructor specifying position, dimensions, and ID.
   *
   * @param x      X coordinate
   * @param y      Y coordinate
   * @param width  width dimension
   * @param height height dimension
   * @param id     unique rectangle ID
   */
  public Rectangle(int x, int y, int width, int height, int id) {
    this(x, y, width, height, id, 0, 0);
  }

  /**
   * Primitive constructor specifying position and dimensions.
   *
   * @param x      X coordinate
   * @param y      Y coordinate
   * @param width  width dimension
   * @param height height dimension
   */
  public Rectangle(int x, int y, int width, int height) {
    this(x, y, width, height, 0, 0, 0);
  }

  /** Default no-arg constructor required for Jackson JSON deserialization. */
  public Rectangle() {
    this(0, 0, 0, 0, 0, 0, 0);
  }

  // =========================================================================
  // GROUP 2: TEMPLATE COPY CONSTRUCTORS (Deriving from an existing Rectangle)
  // =========================================================================

  /**
   * Base Copy Constructor creating an exact deep copy of an existing rectangle.
   *
   * @param other template rectangle to copy
   */
  public Rectangle(Rectangle other) {
    this(other != null ? other.x : 0,
        other != null ? other.y : 0,
        other != null ? other.width : 0,
        other != null ? other.height : 0,
        other != null ? other.id : 0,
        other != null ? other.assignment : 0,
        other != null ? other.number : 0);
  }

  /**
   * Copy Constructor with position, dimension, and stock assignment overrides.
   *
   * @param template   template rectangle for metadata (id, number)
   * @param x          new X coordinate
   * @param y          new Y coordinate
   * @param width      new width dimension
   * @param height     new height dimension
   * @param assignment target stock sheet ID assignment
   */
  public Rectangle(Rectangle template, int x, int y, int width, int height, int assignment) {
    this(x, y, width, height, template.id, assignment, template.number);
  }

  /**
   * Copy Constructor with position and dimension overrides.
   * Preserves template's stock assignment.
   *
   * @param template template rectangle for metadata
   * @param x        new X coordinate
   * @param y        new Y coordinate
   * @param width    new width dimension
   * @param height   new height dimension
   */
  public Rectangle(Rectangle template, int x, int y, int width, int height) {
    this(template, x, y, width, height, template.assignment);
  }

  /**
   * Copy Constructor with position and stock assignment overrides.
   * Preserves template's dimensions.
   *
   * @param template   template rectangle for dimensions and metadata
   * @param x          new X coordinate
   * @param y          new Y coordinate
   * @param assignment target stock sheet ID assignment
   */
  public Rectangle(Rectangle template, int x, int y, int assignment) {
    this(template, x, y, template.width, template.height, assignment);
  }

  // =========================================================================
  // GETTERS & SETTERS
  // =========================================================================

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  /**
   * Calculates the surface area of the rectangle (width * height).
   *
   * @return total surface area
   */
  public int getSize() {
    return width * height;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getId() {
    return id;
  }

  @JsonProperty("assignment")
  public int getAssignment() {
    return assignment;
  }

  public int getNumber() {
    return number;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void setId(int id) {
    this.id = id;
  }

  @JsonProperty("assignment")
  public void setAssignment(int assignment) {
    this.assignment = assignment;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  /**
   * Attempts to merge another touching, aligned rectangle into this rectangle.
   * Modifies this rectangle's dimensions in-place and returns true if merged
   * successfully.
   *
   * @param other target rectangle to attempt merge with
   * @return true if merged successfully, false otherwise
   */
  public boolean tryMerge(Rectangle other, boolean preferVerticalCuts) {
    if (other == null) {
      return false;
    }
    // Horizontal adjacency (same Y and height, touching side-by-side)
    // Disable when Vertical cuts are activated
    if (!preferVerticalCuts) {
      if (this.y == other.y && this.height == other.height) {
        if (this.x + this.width == other.x) {
          this.width += other.width;
          return true;
        }
        if (other.x + other.width == this.x) {
          this.x = other.x;
          this.width += other.width;
          return true;
        }
      }
    }
    // Vertical adjacency (same X and width, touching top-to-bottom)
    if (this.x == other.x && this.width == other.width) {
      if (this.y + this.height == other.y) {
        this.height += other.height;
        return true;
      }
      if (other.y + other.height == this.y) {
        this.y = other.y;
        this.height += other.height;
        return true;
      }
    }
    return false;
  }

  /**
   * Compares two rectangles by surface area descending to satisfy Comparable.
   *
   * @param o target rectangle to compare
   * @return negative if this is larger, positive if smaller, 0 if equal
   */
  @Override
  public int compareTo(Rectangle o) {
    return Integer.compare(o.getSize(), this.getSize());
  }
}
