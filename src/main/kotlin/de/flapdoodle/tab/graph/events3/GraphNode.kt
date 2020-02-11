package de.flapdoodle.tab.graph.events3

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import tornadofx.*

class GraphNode(
    private val x: Number = 0.0,
    private val y: Number = 0.0
) : Fragment() {
  override val root = group {
    rectangle(x = x, y = y) {
      style {
        fill = Color.BLUE
        width = 20.0
        height = 20.0
      }

      onMouseEntered = EventHandler {
        println("entered.. try sending event")
        fire(EnterNodeEvent(this@GraphNode))
      }
    }
  }

  data class EnterNodeEvent(val parent: GraphNode) : FXEvent()
}