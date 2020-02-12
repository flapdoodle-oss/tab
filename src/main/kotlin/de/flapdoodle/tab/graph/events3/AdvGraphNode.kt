package de.flapdoodle.tab.graph.events3

import javafx.geometry.Point2D
import javafx.scene.Parent
import javafx.scene.paint.Color
import tornadofx.*

class AdvGraphNode(
    private val x: Double = 0.0,
    private val y: Double = 0.0
) : Fragment() {
  override val root = group {
    relocate(x.toDouble(), y.toDouble())

    borderpane {
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
            width = 8.0
            height = 8.0
          }
        }
      }
    }
  }

  fun position(): Point2D {
    return Point2D(root.layoutX, root.layoutY)
  }

  fun moveTo(x: Double, y: Double) {
    root.relocate(x, y)
  }

  data class Move(val parent: AdvGraphNode): IsMarker
}