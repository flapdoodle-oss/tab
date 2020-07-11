package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.fx.bindings.mapNullable
import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import tornadofx.*

class ConnectionNode(
    private val start: ObservableValue<Point2D>,
    private val end: ObservableValue<Point2D>
) : Fragment() {
  override val root = group {
    val startX = start.mapNullable { it!!.x }
    val startY = start.mapNullable { it!!.y }
    val endX = end.mapNullable { it!!.x }
    val endY = end.mapNullable { it!!.y }

    circle {
      centerXProperty().bind(startX)
      centerYProperty().bind(startY)
      fill = Color(0.3, 0.5, 0.3, 0.3)
      radius = 8.0
      isMouseTransparent = true
    }

    circle {
      centerXProperty().bind(endX)
      centerYProperty().bind(endY)
      fill = Color(0.5, 0.3, 0.3, 0.3)
      radius = 8.0
      isMouseTransparent = true
    }

    line {
      startXProperty().bind(startX)
      startYProperty().bind(startY)
      endXProperty().bind(endX)
      endYProperty().bind(endY)

      stroke = Color(0.3, 0.3, 0.3, 0.3)
      strokeWidth = 3.0
      isMouseTransparent = true
    }
  }
}