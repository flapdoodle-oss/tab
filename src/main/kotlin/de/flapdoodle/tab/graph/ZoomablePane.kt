package de.flapdoodle.tab.graph

import de.flapdoodle.tab.graph.events.MouseDragListener
import de.flapdoodle.tab.graph.events.MouseDragListenerLookup
import de.flapdoodle.tab.graph.events.MouseEvents
import de.flapdoodle.tab.graph.events2.MouseEventHandler
import de.flapdoodle.tab.graph.events2.MouseEventHandlerResolver
import de.flapdoodle.tab.graph.events3.AdvGraphNode
import de.flapdoodle.tab.graph.events3.GraphNode
import de.flapdoodle.tab.graph.events3.HasMarker
import de.flapdoodle.tab.graph.events3.IsMarker
import de.flapdoodle.tab.graph.events3.MappedMouseEvent
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Bounds
import javafx.geometry.Point2D
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
    Zoomable.enableDrag(this, content)

    val lookup = MouseDragListenerLookup.forType<Rectangle> {
      val start = javafx.geometry.Point2D(it.x, it.y)
      it.apply {
        style {
          fill = Color.RED
        }
      }
      MouseDragListener(done = {
        it.apply {
          style {
            fill = Color.YELLOW
          }
        }
      }) { x, y, target ->
        if (target != it) {
          println("connect to $target?")
        }
        it.x = start.x + x
        it.y = start.y + y
      }
    }

    if (false) {
      MouseEvents.addEventDelegate(this, scale, lookup)
    }

    val lookup2 = MouseEventHandlerResolver.forType<Rectangle> { it ->
      it.apply {
        style {
          fill = Color.RED
        }
      }
      object : MouseEventHandler {
        override fun onExit(eventTarget: EventTarget): MouseEventHandler? {
          it.apply {
            style {
              fill = Color.YELLOW
            }
          }
          return null
        }
      }
    }

    if (false) {
      de.flapdoodle.tab.graph.events2.MouseEvents.addEventDelegate(this, scale, lookup2)
    }

    val resolver = de.flapdoodle.tab.graph.events3.MouseEventHandlerResolver.forType<AdvGraphNode.Move> { moveMarker ->
      object : de.flapdoodle.tab.graph.events3.MouseEventHandler {
        var dragStarted: Point2D? = null
        var exited: Boolean = false

        override fun onEvent(mouseEvent: MappedMouseEvent, marker: IsMarker?): de.flapdoodle.tab.graph.events3.MouseEventHandler? {
          println("$mouseEvent -> $marker")
          when (mouseEvent) {
            is MappedMouseEvent.Click -> dragStarted = moveMarker.parent.position()
            is MappedMouseEvent.Drag -> dragStarted?.let { it + mouseEvent.delta }?.apply {
              println("should move ${moveMarker.parent} by ${mouseEvent.delta}")
              moveMarker.parent.moveTo(this.x, this.y)
            }
            is MappedMouseEvent.Release -> dragStarted = null
            is MappedMouseEvent.Exit -> exited = exited || marker == moveMarker
          }

          return if (exited && dragStarted==null) {
            println("exit $moveMarker because no drag in progress")
            null
          } else
            this
        }
      }
    }
    HasMarker.addEventDelegate(this,scale, resolver)

    subscribe<GraphNode.EnterNodeEvent> { event ->
      println("entered: ${event.parent}")
    }
  }
}
