package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.graph.events.marker
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*

object Connections {
  fun nodeFor(vararg inputs: Input<*>): Node {
    return VBox().apply {
      spacing = 4.0

      inputs.forEach { input ->
        nodeFor(input) {
          fill = Color.YELLOW
          width = 8.0
          height = 8.0
        }
      }
    }
  }

  fun nodeFor(vararg outputs: Output<*>): Node {
    return VBox().apply {
      spacing = 4.0

      outputs.forEach { output ->
        nodeFor(output) {
          fill = Color.RED
          width = 8.0
          height = 8.0
        }
      }
    }
  }

  private fun Parent.nodeFor(input: Input<*>, op: Rectangle.() -> Unit = {}) {
    rectangle(op = op).apply {
      marker = input
    }
  }

  private fun Parent.nodeFor(output: Output<*>, op: Rectangle.() -> Unit = {}) {
    rectangle(op = op).apply {
      marker = output
    }
  }
}