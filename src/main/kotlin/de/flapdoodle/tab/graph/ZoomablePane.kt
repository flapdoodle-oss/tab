package de.flapdoodle.tab.graph

import de.flapdoodle.tab.graph.events3.AdvGraphNode
import de.flapdoodle.tab.graph.events3.GraphNode
import de.flapdoodle.tab.graph.events3.HasMarker
import de.flapdoodle.tab.graph.events3.IsMarker
import de.flapdoodle.tab.graph.events3.MappedMouseEvent
import de.flapdoodle.tab.graph.events3.MouseEventHandler
import de.flapdoodle.tab.graph.events3.MouseEventHandlerResolver
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Bounds
import javafx.geometry.Point2D
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

    val resolver = MouseEventHandlerResolver.forType<AdvGraphNode.Move> { moveMarker ->
      object : MouseEventHandler {
        var dragStarted: Point2D? = null
        var exited: Boolean = false

        override fun onEvent(mouseEvent: MappedMouseEvent, marker: IsMarker?): MouseEventHandler? {
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

          return if (exited && dragStarted == null) {
            println("exit $moveMarker because no drag in progress")
            null
          } else
            this
        }
      }
    }.andThen(MouseEventHandlerResolver.forType<AdvGraphNode.Resize> { resizeMarker ->
      object : MouseEventHandler {
        var dragStarted: Point2D? = null
        var exited: Boolean = false

        override fun onEvent(mouseEvent: MappedMouseEvent, marker: IsMarker?): MouseEventHandler? {
          println("$mouseEvent -> $marker")
          when (mouseEvent) {
            is MappedMouseEvent.Click -> dragStarted = resizeMarker.parent.size()
            is MappedMouseEvent.Drag -> dragStarted?.let { it + mouseEvent.delta }?.apply {
              println("should move ${resizeMarker.parent} by ${mouseEvent.delta}")
              resizeMarker.parent.resizeTo(this.x, this.y)
            }
            is MappedMouseEvent.Release -> dragStarted = null
            is MappedMouseEvent.Exit -> exited = exited || marker == resizeMarker
          }

          return if (exited && dragStarted == null) {
            println("exit $resizeMarker because no drag in progress")
            null
          } else
            this
        }
      }
    })

    HasMarker.addEventDelegate(this, scale, resolver)

    subscribe<GraphNode.EnterNodeEvent> { event ->
      println("entered: ${event.parent}")
    }
  }
}
