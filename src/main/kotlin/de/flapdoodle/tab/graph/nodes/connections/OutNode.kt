package de.flapdoodle.tab.graph.nodes.connections

import de.flapdoodle.tab.graph.events.marker
import javafx.scene.Group
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.*

class OutNode(out: Out, color: Color = Color.ORANGE) : StackPane() {
  init {
    apply {
      marker = out
      style {
        padding = box(2.0.px)
      }

      circle {
        style {
          fill = color
          radius = 4.0
        }
      }
    }
  }
}