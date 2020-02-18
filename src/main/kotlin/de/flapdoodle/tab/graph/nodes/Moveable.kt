package de.flapdoodle.tab.graph.nodes

import javafx.geometry.Point2D

interface Moveable {
  fun position(): Point2D
  fun moveTo(x: Double, y: Double)
}