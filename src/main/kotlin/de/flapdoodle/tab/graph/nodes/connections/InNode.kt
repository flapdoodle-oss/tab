package de.flapdoodle.tab.graph.nodes.connections

import de.flapdoodle.tab.data.values.Variable
import de.flapdoodle.tab.graph.events.marker
import javafx.scene.Group
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.*

class InNode(variableInput: VariableInput<out Any>, color: Color = Color.DARKGREEN) : StackPane() {
  init {
    apply {
      marker = variableInput
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