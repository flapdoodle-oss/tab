package de.flapdoodle.tab.graph.events3

import javafx.scene.Parent
import javafx.scene.paint.Color
import tornadofx.*

class AdvGraphNode(
    private val x: Number = 0.0,
    private val y: Number = 0.0
) : Fragment() {
  override val root = group {
    borderpane {
      relocate(x.toDouble(), y.toDouble())
      style {
        fill = Color.PURPLE
      }

      center = button {
        text = "Woohoo"
      }

      bottom = markedGroup(Move(this@AdvGraphNode)) {
        rectangle {
          style {
            fill = Color.RED
            width = 4.0
            height = 4.0
          }
        }
      }
    }
  }

  data class Move(val parent: AdvGraphNode): IsMarker
}