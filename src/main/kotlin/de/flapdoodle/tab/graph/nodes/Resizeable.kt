package de.flapdoodle.tab.graph.nodes

import javafx.geometry.Dimension2D

interface Resizeable {
  fun size(): Dimension2D
  fun resizeTo(width: Double, height: Double)
}