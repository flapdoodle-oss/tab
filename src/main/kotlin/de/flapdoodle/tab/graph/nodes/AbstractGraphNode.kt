package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.graph.events.IsMarker
import de.flapdoodle.tab.graph.events.markedGroup
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.*

abstract class AbstractGraphNode(
    private val x: Double = 0.0,
    private val y: Double = 0.0
) : Fragment() {

  private val header = markedGroup(Move(this)) {
    hbox {
      useMaxWidth = true

      label {
        textProperty().bind(this@AbstractGraphNode.titleProperty)
      }
    }
  }

  private val footer = hbox {
    alignment = Pos.BOTTOM_RIGHT

//    label {
//      text = "..."
//      useMaxWidth = true
//      hgrow = Priority.ALWAYS
//    }

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
    relocate(x,y)

    style(append = true) {
      backgroundColor += Color.WHITE
      borderColor += box(
          top = Color.RED,
          right = Color.DARKGREEN,
          left = Color.ORANGE,
          bottom = Color.PURPLE
      )

      borderWidth += box(0.5.px)
      borderRadius += box(5.0.px)
    }

    top = header
    center = rectangle {
      style {
        width = 30.0
        height = 30.0
        fill = Color.BLUE
      }
    }
    bottom = footer
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