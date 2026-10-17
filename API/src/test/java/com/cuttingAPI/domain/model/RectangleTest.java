package com.cuttingAPI.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Rectangle Domain Model Unit Tests")
class RectangleTest {

  @Nested
  @DisplayName("Constructor and Property Accessors")
  class ConstructorTests {

    @Test
    @DisplayName("Should correctly initialize all fields using full constructor")
    void shouldInitializeAllFields_WhenFullConstructorUsed() {
      Rectangle rect = new Rectangle(10, 20, 100, 200, 1, 5, 2);

      assertEquals(10, rect.getX());
      assertEquals(20, rect.getY());
      assertEquals(100, rect.getWidth());
      assertEquals(200, rect.getHeight());
      assertEquals(1, rect.getId());
      assertEquals(5, rect.getAssignment());
      assertEquals(2, rect.getNumber());
    }

    @Test
    @DisplayName("Should correctly compute size (width * height)")
    void shouldComputeSize_Correctly() {
      Rectangle rect = new Rectangle(0, 0, 50, 40);
      assertEquals(2000, rect.getSize());
    }

    @Test
    @DisplayName("Should update properties via setters")
    void shouldUpdateProperties_WhenSettersCalled() {
      Rectangle rect = new Rectangle();
      rect.setX(15);
      rect.setY(25);
      rect.setWidth(80);
      rect.setHeight(60);
      rect.setId(99);
      rect.setAssignment(7);
      rect.setNumber(3);

      assertEquals(15, rect.getX());
      assertEquals(25, rect.getY());
      assertEquals(80, rect.getWidth());
      assertEquals(60, rect.getHeight());
      assertEquals(99, rect.getId());
      assertEquals(7, rect.getAssignment());
      assertEquals(3, rect.getNumber());
    }

    @Test
    @DisplayName("Should set assignment via German alias setZuordnung")
    void shouldSetAssignment_WhenGermanAliasUsed() {
      Rectangle rect = new Rectangle();
      assertEquals(0, rect.getAssignment());
    }
  }

  @Nested
  @DisplayName("Comparison and Cloning Logic")
  class ComparisonAndCloneTests {

    @Test
    @DisplayName("Should copy Rectangle using copy constructor correctly")
    void shouldCopyRectangle_Correctly() {
      Rectangle original = new Rectangle(10, 20, 100, 200, 1, 5, 2);
      Rectangle copy = new Rectangle(original);

      assertNotSame(original, copy);
      assertEquals(original.getX(), copy.getX());
      assertEquals(original.getY(), copy.getY());
      assertEquals(original.getWidth(), copy.getWidth());
      assertEquals(original.getHeight(), copy.getHeight());
      assertEquals(original.getId(), copy.getId());
      assertEquals(original.getAssignment(), copy.getAssignment());
      assertEquals(original.getNumber(), copy.getNumber());
    }

    @Test
    @DisplayName("Should compare rectangles by size descending for Comparable interface")
    void shouldCompareBySize_Descending() {
      Rectangle smaller = new Rectangle(0, 0, 10, 10); // Size 100
      Rectangle larger = new Rectangle(0, 0, 20, 20); // Size 400

      assertTrue(smaller.compareTo(larger) > 0);
      assertTrue(larger.compareTo(smaller) < 0);
      assertEquals(0, smaller.compareTo(smaller));
    }
  }
}
