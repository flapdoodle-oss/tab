package de.flapdoodle.tab.graph.nodes.connections

import de.flapdoodle.tab.graph.events.marker
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.*

class InNode(val input: In<out Any>, color: Color = Color.DARKGREEN) : StackPane() {
  private val handle = circle {
    style {
      fill = color
      radius = 4.0
    }
    isMouseTransparent = true
  }

  init {
    apply {
      marker = input
      style {
        padding = box(2.0.px)
      }

      this += handle
    }
  }

  fun centerX() = handle.centerXProperty()
  fun centerY() = handle.centerXProperty()
}