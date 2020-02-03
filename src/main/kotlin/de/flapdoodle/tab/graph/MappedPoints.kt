package de.flapdoodle.tab.graph

import javafx.geometry.Point2D

data class ScaledPoints(
    private val screenCoord: Point2D,
    private val localCoord: Point2D
) {

  constructor(screen_x: Double, screen_y: Double, local_x: Double, local_y: Double) : this(Point2D(screen_x, screen_y), Point2D(local_x, local_y)) {}

  fun scaledLocalCoord(newScreenCoord: Point2D, scale: Double): Point2D {
    val screenDiff = newScreenCoord.subtract(screenCoord)
    val layoutDiff = Point2D(screenDiff.x / scale, screenDiff.y / scale)
    return localCoord.add(layoutDiff)
  }

  override fun toString(): String {
    return ("ScaledPoints(" + screenCoord.x + "," + screenCoord.y + ")->(" + localCoord.x + ","        + localCoord.y + ")")
  }
}