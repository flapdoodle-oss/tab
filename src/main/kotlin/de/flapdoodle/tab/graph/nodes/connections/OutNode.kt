package de.flapdoodle.tab.graph.nodes.connections

import de.flapdoodle.tab.graph.events.marker
import javafx.scene.Group
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.*

class OutNode(val out: Out, color: Color = Color.ORANGE) : StackPane() {
  private val handle = circle {
    style {
      fill = color
      radius = 4.0
    }
    isMouseTransparent = true
  }

  init {
    apply {
      marker = out
      style {
        padding = box(2.0.px)
      }

      this += handle
    }
  }

  fun centerX() = handle.layoutXProperty()
  fun centerY() = handle.layoutYProperty()
}