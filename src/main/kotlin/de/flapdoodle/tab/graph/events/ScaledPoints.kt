package de.flapdoodle.tab.graph.events

import javafx.geometry.Point2D

data class ScaledPoints(
    private val screenCoord: Point2D
) {

  constructor(screen_x: Double, screen_y: Double) : this(Point2D(screen_x, screen_y))

  fun scaledCoord(newScreenCoord: Point2D, scale: Double): Point2D {
    val screenDiff = newScreenCoord.subtract(screenCoord)
    return Point2D(screenDiff.x / scale, screenDiff.y / scale)
  }

  override fun toString(): String {
    return ("ScaledPoints(" + screenCoord.x + "," + screenCoord.y + ")")
  }
}