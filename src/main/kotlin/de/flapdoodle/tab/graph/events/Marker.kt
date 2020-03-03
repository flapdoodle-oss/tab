package de.flapdoodle.tab.graph.events

import de.flapdoodle.tab.extensions.scaledChange
import javafx.beans.property.DoubleProperty
import javafx.event.EventTarget
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseDragEvent
import javafx.scene.input.MouseEvent

object Marker {

  fun markerResolver(eventTarget: EventTarget, searchInParent: Boolean = false): IsMarker? {
    return when (eventTarget) {
      is HasMarker<*> -> eventTarget.marker()
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
          val currentCoord = Point2D(event.x, event.y)
          start = currentCoord

          currentHandler = currentHandler?.let {
            event.consume()

            it.onEvent(MappedMouseEvent.Click(event, currentCoord), marker)
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
            val scaledPos = lastPos.scaledChange(Point2D(event.x, event.y), scale.value)
//            if (event.eventType == MouseEvent.MOUSE_RELEASED) {
//              start = null
//            }
            val mappedEvent = when (event.eventType) {
              MouseEvent.DRAG_DETECTED -> MappedMouseEvent.DragDetected(event, scaledPos)
              MouseEvent.MOUSE_DRAGGED -> MappedMouseEvent.Drag(event, scaledPos)
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

  fun addEventDelegateX(root: Node, scale: DoubleProperty, resolver: MouseEventHandlerResolver) {
    var currentHandler: MouseEventHandler? = null
    var start: Point2D? = null

    root.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET) { event ->
      val eventTarget = event.target
      val marker = markerResolver(eventTarget)

      if (marker != null) {
        println("enter marker: $marker")
        System.out.flush()

        currentHandler = currentHandler.let { it ->
          it ?: resolver.onEnter(marker)?.let {
            event.consume()

            it.onEvent(MappedMouseEvent.Enter(event), marker)
          }
        }
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_PRESSED) { click ->
      val eventTarget = click.target
      val marker = markerResolver(eventTarget)

      currentHandler = currentHandler?.let {
        click.consume()

        val currentCoord = Point2D(click.x, click.y)
        start = currentCoord
        it.onEvent(MappedMouseEvent.Click(click, currentCoord), marker)
      }
    }

    root.addEventFilter(MouseEvent.DRAG_DETECTED) { drag ->
      val eventTarget = drag.target
      val marker = markerResolver(eventTarget)

      currentHandler = currentHandler?.let {
        drag.consume()

        val lastPos = start
        require(lastPos != null) { "start not set" }
        val scaledPos = lastPos.scaledChange(Point2D(drag.x, drag.y), scale.value)
        it.onEvent(MappedMouseEvent.DragDetected(drag, scaledPos), marker)
      }
    }

    root.addEventFilter(MouseDragEvent.ANY) { drag ->
      val eventTarget = drag.target
      val marker = markerResolver(eventTarget)

      currentHandler = currentHandler?.let {
        drag.consume()

        val lastPos = start
        require(lastPos != null) { "start not set" }
        val scaledPos = lastPos.scaledChange(Point2D(drag.x, drag.y), scale.value)
        it.onEvent(MappedMouseEvent.DragDetected(drag, scaledPos), marker)
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_DRAGGED) { drag ->
      val eventTarget = drag.target
      val marker = markerResolver(eventTarget)

      currentHandler = currentHandler?.let {
        drag.consume()

        val lastPos = start
        require(lastPos != null) { "start not set" }
        val scaledPos = lastPos.scaledChange(Point2D(drag.x, drag.y), scale.value)
        it.onEvent(MappedMouseEvent.Drag(drag, scaledPos), marker)
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_RELEASED) { release ->
      val eventTarget = release.target
      val marker = markerResolver(eventTarget)

      currentHandler = currentHandler?.let {
        release.consume()

        val lastPos = start
        require(lastPos != null) { "start not set" }
        val scaledPos = lastPos.scaledChange(Point2D(release.x, release.y), scale.value)
        start = null
        it.onEvent(MappedMouseEvent.Release(release, scaledPos), marker)
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET) { event ->
      val eventTarget = event.target
      val marker = markerResolver(eventTarget)

      if (marker != null) {
        println("exit marker: $marker")
        System.out.flush()
        currentHandler = currentHandler?.let {
          event.consume()

          it.onEvent(MappedMouseEvent.Exit(event), marker)
        }
      }
    }
  }
}