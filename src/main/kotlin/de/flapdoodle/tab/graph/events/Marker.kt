package de.flapdoodle.tab.graph.events

import de.flapdoodle.tab.extensions.scaledChange
import javafx.beans.property.DoubleProperty
import javafx.event.EventTarget
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent

object Marker {

  val markerResolver: (EventTarget) -> IsMarker? = { eventTarget ->
    when (eventTarget) {
      is HasMarker<*> -> eventTarget.marker()
      is Node -> HasMarkerProperty.markerOf(eventTarget)
      else -> null
    }
  }

  fun addEventDelegate(root: Node, scale: DoubleProperty, resolver: MouseEventHandlerResolver) {
    var currentHandler: MouseEventHandler? = null
    var start: Point2D? = null

    root.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET) { event ->
      val eventTarget = event.target
      val marker = markerResolver(eventTarget)

      if (marker != null) {
        println("enter marker: $marker")

        currentHandler = currentHandler.let { it ->
          it ?: resolver.onEnter(marker)?.let {
            event.consume()

            it.onEvent(MappedMouseEvent.Enter(), marker)
          }
        }
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_PRESSED) { click ->
      currentHandler = currentHandler?.let {
        click.consume()

        val currentCoord = Point2D(click.x, click.y)
        start = currentCoord
        it.onEvent(MappedMouseEvent.Click(currentCoord), null)
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_DRAGGED) { drag ->
      currentHandler = currentHandler?.let {
        drag.consume()

        val lastPos = start
        require(lastPos!=null) {"start not set"}
        val scaledPos = lastPos.scaledChange(Point2D(drag.x, drag.y), scale.value)
        it.onEvent(MappedMouseEvent.Drag(scaledPos), null)
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_RELEASED) { release ->
      currentHandler = currentHandler?.let {
        release.consume()

        val lastPos = start
        require(lastPos!=null) {"start not set"}
        val scaledPos = lastPos.scaledChange(Point2D(release.x, release.y), scale.value)
        start=null
        it.onEvent(MappedMouseEvent.Release(scaledPos), null)
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET) { event ->
      val eventTarget = event.target
      val marker = markerResolver(eventTarget)

      if (marker != null) {
        println("exit marker: $marker")
        currentHandler = currentHandler?.let {
          event.consume()

          it.onEvent(MappedMouseEvent.Exit(), marker)
        }
      }
    }
  }
}