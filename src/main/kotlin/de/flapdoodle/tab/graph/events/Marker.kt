package de.flapdoodle.tab.graph.events

import com.sun.javafx.collections.MappingChange
import de.flapdoodle.tab.extensions.fire
import de.flapdoodle.tab.extensions.scaledChange
import de.flapdoodle.tab.graph.MappedPoints
import de.flapdoodle.tab.graph.nodes.renderer.events.ExplainEvent
import javafx.beans.property.DoubleProperty
import javafx.event.EventTarget
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseDragEvent
import javafx.scene.input.MouseEvent

object Marker {

  fun markerResolver(eventTarget: EventTarget, searchInParent: Boolean = false): IsMarker? {
    return when (eventTarget) {
      is Node -> HasMarkerProperty.markerOf(eventTarget, searchInParent)
      else -> null
    }
  }

  fun addEventDelegate(root: Node, scale: DoubleProperty, resolver: MouseEventHandlerResolver) {
    var currentHandler: MouseEventHandler? = null
    var start: Point2D? = null

    root.addEventFilter(MouseEvent.ANY) { event ->
      val eventTarget = event.target
      val marker = markerResolver(eventTarget)

//      println("event ${event.eventType}, marker: $marker, eventTarget: $eventTarget")

      val mouseCoord = Point2D(event.x, event.y)

      when (event.eventType) {
        MouseEvent.MOUSE_ENTERED_TARGET -> {
          if (marker != null) {
            currentHandler = currentHandler.let { it ->
              (it ?: resolver.onEnter(marker))?.let {
                event.consume()

                it.onEvent(MappedMouseEvent.Enter(event), marker)
              }
            }
          }
        }
        MouseEvent.MOUSE_PRESSED -> {
          start = mouseCoord

          currentHandler = currentHandler?.let {
            event.consume()

            it.onEvent(MappedMouseEvent.Click(event, mouseCoord), marker)
          }
        }
        MouseEvent.MOUSE_EXITED_TARGET -> {
          currentHandler = currentHandler?.let {
            event.consume()

            it.onEvent(MappedMouseEvent.Exit(event), marker)
          }
        }
        MouseEvent.MOUSE_MOVED, MouseEvent.MOUSE_EXITED, MouseEvent.MOUSE_ENTERED, MouseEvent.MOUSE_CLICKED -> {

        }
        MouseDragEvent.MOUSE_DRAG_ENTERED, MouseDragEvent.MOUSE_DRAG_EXITED, MouseDragEvent.MOUSE_DRAG_OVER -> {

        }
        else -> {
          currentHandler = currentHandler?.let {
            event.consume()

            val lastPos = start
            require(lastPos != null) { "start not set with $event" }
            val scaledPos = lastPos.scaledChange(mouseCoord, scale.value)
//            if (event.eventType == MouseEvent.MOUSE_RELEASED) {
//              start = null
//            }
            val mappedEvent = when (event.eventType) {
              MouseEvent.DRAG_DETECTED -> MappedMouseEvent.DragDetected(event, scaledPos)
              MouseEvent.MOUSE_DRAGGED -> MappedMouseEvent.Drag(event, scaledPos, mouseCoord)
              MouseEvent.MOUSE_RELEASED -> MappedMouseEvent.Release(event, scaledPos)
              MouseDragEvent.MOUSE_DRAG_ENTERED_TARGET -> MappedMouseEvent.DragEnter(event, scaledPos)
              MouseDragEvent.MOUSE_DRAG_EXITED_TARGET -> MappedMouseEvent.DragExit(event, scaledPos)
              MouseDragEvent.MOUSE_DRAG_RELEASED -> MappedMouseEvent.DragRelease(event, scaledPos)
              else -> throw IllegalArgumentException("not supported: $event")
            }
            it.onEvent(mappedEvent, marker)
          }

        }
      }

    }
  }
}