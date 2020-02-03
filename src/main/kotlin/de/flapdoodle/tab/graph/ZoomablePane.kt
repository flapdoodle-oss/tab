package de.flapdoodle.tab.graph

import de.flapdoodle.tab.graph.events.MouseDragListener
import de.flapdoodle.tab.graph.events.MouseDragListenerLookup
import de.flapdoodle.tab.graph.events.MouseEvents
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.geometry.Bounds
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*

class ZoomablePane : Fragment("My View") {
  private val scale = SimpleDoubleProperty(1.0)

  val content = pane {
    prefWidth = 100.0
    prefHeight = 100.0

    scaleXProperty().bind(scale)
    scaleYProperty().bind(scale)
  }

  override val root = pane {
    children += content

    val outputClip = Rectangle()
    clip = outputClip

    layoutBoundsProperty().addListener { ov: ObservableValue<out Bounds>?, oldValue: Bounds?, newValue: Bounds ->
      outputClip.width = newValue.width
      outputClip.height = newValue.height
    }

    style(append = true) {
      borderColor += box(
          top = Color.RED,
          right = Color.DARKGREEN,
          left = Color.ORANGE,
          bottom = Color.PURPLE
      )

      borderWidth += box(0.5.px)
    }

    Zoomable.enableZoom(this, scale)
    Zoomable.enableDrag(this,content)

    val lookup = MouseDragListenerLookup.forType<Rectangle> {
      val start = javafx.geometry.Point2D(it.x, it.y)
      MouseDragListener { x,y, target ->
        println("move to $x,$y")
        it.x = start.x + x
        it.y = start.y + y
      }
    }

    MouseEvents.addEventDelegate(this, scale, lookup)
  }
}
