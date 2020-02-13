package de.flapdoodle.tab.graph.events3

import de.flapdoodle.tab.graph.events.IsMarker
import de.flapdoodle.tab.graph.events.markedGroup
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*

class AdvGraphNode(
    private val x: Double = 0.0,
    private val y: Double = 0.0
) : Fragment() {

  private val rect = Rectangle().apply {
    style {
      fill = Color.YELLOW
      width = 30.0
      height = 30.0
    }
  }

  private val content =
      borderpane {
        relocate(x, y)

        style {
          fill = Color.PURPLE
        }

        center = rect

        top = button {
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

        right = markedGroup(Resize(this@AdvGraphNode)) {
          rectangle {
            style {
              fill = Color.GREEN
              width = 8.0
              height = 8.0
            }
          }
        }
      }


  override val root = group {
    this += content
  }

  fun position(): Point2D {
    return Point2D(content.layoutX, content.layoutY)
  }

  fun moveTo(x: Double, y: Double) {
    content.relocate(x, y)
  }

  fun size(): Point2D {
    return Point2D(content.width, content.height)
  }

  fun resizeTo(width: Double, height: Double) {
    content.setPrefSize(width, height)
  }

  data class Move(val parent: AdvGraphNode) : IsMarker
  data class Resize(val parent: AdvGraphNode) : IsMarker
}