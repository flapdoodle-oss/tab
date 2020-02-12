package de.flapdoodle.tab.graph.events3

import de.flapdoodle.tab.extensions.change
import de.flapdoodle.tab.extensions.matches
import de.flapdoodle.tab.extensions.scaledChange
import de.flapdoodle.tab.graph.events.MouseEvents
import javafx.beans.property.DoubleProperty
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent

interface HasMarker<M : IsMarker> {
  fun marker(): M

  companion object {
    fun addEventDelegate(root: Node, scale: DoubleProperty, resolver: MouseEventHandlerResolver) {
      var currentHandler: MouseEventHandler? = null
      var start: Point2D? = null

      root.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET) { event ->
        val eventTarget = event.target

        if (eventTarget is HasMarker<*>) {
          val marker = eventTarget.marker()
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

        if (eventTarget is HasMarker<*>) {
          val marker = eventTarget.marker()

          println("exit marker: $marker")
          currentHandler = currentHandler?.let {
            event.consume()

            it.onEvent(MappedMouseEvent.Exit(), marker)
          }
        }
      }
    }
  }
}