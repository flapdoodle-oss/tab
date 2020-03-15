package de.flapdoodle.tab.extensions

import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent

fun Point2D.scaledChange(other: Point2D, scale: Double): Point2D {
  val diff = other.subtract(this)
  return Point2D(diff.x / scale, diff.y / scale)
}

fun MouseEvent.screenCoords(): Point2D {
  return Point2D(this.screenX, this.screenY)
}

