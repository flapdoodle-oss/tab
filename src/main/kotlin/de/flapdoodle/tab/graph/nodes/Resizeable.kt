package de.flapdoodle.tab.graph.nodes

import javafx.geometry.Point2D

interface Resizeable {
  fun size(): Point2D
  fun resizeTo(width: Double, height: Double)
}