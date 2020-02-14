package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.graph.events.HasMarkerProperty
import de.flapdoodle.tab.graph.events.IsMarker
import de.flapdoodle.tab.graph.events.markedGroup
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.paint.Color
import tornadofx.*

abstract class AbstractGraphNode() : Fragment() {

  internal abstract fun content(): Node

  private val header = hbox {
    HasMarkerProperty.setMarker(this, Move(this@AbstractGraphNode))
    useMaxWidth = true

    label {
      textProperty().bind(this@AbstractGraphNode.titleProperty)
    }
  }

  private val footer = hbox {
    alignment = Pos.BOTTOM_RIGHT

    markedGroup(Resize(this@AbstractGraphNode)) {
      rectangle {
        style {
          fill = Color.GREEN
          width = 8.0
          height = 8.0
        }
      }
    }
  }

  private val window = borderpane {
    autosize()

    style(append = true) {
      backgroundColor += Color.WHITE
      backgroundRadius += box(5.0.px)

      borderColor += box(
          top = Color.RED,
          right = Color.DARKGREEN,
          left = Color.ORANGE,
          bottom = Color.PURPLE
      )

      borderWidth += box(0.5.px)
      borderRadius += box(5.0.px)
    }

    center = borderpane {
      top = header
      center = content()
      bottom = footer
    }
  }

  override val root = group {
    this += window
  }

  fun position(): Point2D {
    return Point2D(window.layoutX, window.layoutY)
  }

  fun moveTo(x: Double, y: Double) {
    window.relocate(x, y)
  }

  fun size(): Point2D {
    return Point2D(window.width, window.height)
  }

  fun resizeTo(width: Double, height: Double) {
    window.setPrefSize(width, height)
  }

  data class Move(val parent: AbstractGraphNode) : IsMarker
  data class Resize(val parent: AbstractGraphNode) : IsMarker
}