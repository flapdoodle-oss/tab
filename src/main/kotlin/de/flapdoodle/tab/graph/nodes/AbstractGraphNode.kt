package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.graph.events.markedGroup
import de.flapdoodle.tab.graph.events.marker
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.effect.DropShadow
import javafx.scene.effect.Reflection
import javafx.scene.paint.Color
import tornadofx.*

abstract class AbstractGraphNode<T : Node>(
    contentFactory: () -> T
) : Fragment(), Moveable, Resizeable {

  private val header = hbox {
    marker = Move(this@AbstractGraphNode)
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
      center = contentFactory()
      bottom = footer
    }
  }

  override val root = group {
    this += window
    effect = DropShadow().apply {
      offsetX = 3.0
      offsetY = 3.0
      radius = 10.0
      color = Color.GRAY
    }

//    effect = Reflection().apply {
//
//    }
  }

  override fun position(): Point2D {
    return Point2D(window.layoutX, window.layoutY)
  }

  override fun moveTo(x: Double, y: Double) {
    window.relocate(x, y)
  }

  override fun size(): Point2D {
    return Point2D(window.width, window.height)
  }

  override fun resizeTo(width: Double, height: Double) {
    window.setPrefSize(width, height)
  }


}