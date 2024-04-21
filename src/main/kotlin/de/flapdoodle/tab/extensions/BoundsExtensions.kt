package de.flapdoodle.tab.extensions

import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.Node

fun Bounds.min(): Point2D {
  return Point2D(this.minX, this.minY)
}

fun Bounds.centerOf(): Point2D {
  val x = (minX + maxX) / 2
  val y = (minY + maxY) / 2
  return Point2D(x, y)
}

fun Node.mappedBounds(child: Node, localBounds: Bounds): Bounds {
  val parent = child.parent
  var bounds = child.localToParent(localBounds)
  if (this !== parent) {
    bounds = this.mappedBounds(parent, bounds)
  }
  return bounds
}

fun Node.boundsInTop(childInTop: Node): Bounds {
  return this.mappedBounds(childInTop, childInTop.boundsInLocal)
}

fun Node.map(childInTop: Node, coord: Point2D): Point2D {
  return this.mappedBounds(childInTop, BoundingBox(coord.x, coord.y, 0.0, 0.0)).min()
}

