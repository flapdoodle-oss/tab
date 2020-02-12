package de.flapdoodle.tab.graph.events3

import javafx.beans.property.DoubleProperty
import javafx.scene.Node
import javafx.scene.input.MouseEvent

interface HasMarker<M : IsMarker> {
  fun marker(): M

  companion object {
    fun addEventDelegate(root: Node, scale: DoubleProperty, resolver: MouseEventHandlerResolver) {
      var currentHandler: MouseEventHandler? = null

      root.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET) { event ->
        val eventTarget = event.target

        if (eventTarget is HasMarker<*>) {
          val marker = eventTarget.marker()
          println("enter marker: $marker")

          currentHandler = resolver.onEnter(marker)?.onEvent(event, marker)
        }
      }

      root.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET) { event ->
        val eventTarget = event.target

        if (eventTarget is HasMarker<*>) {
          val marker = eventTarget.marker()

          println("exit marker: $marker")
          currentHandler = currentHandler?.onEvent(event, marker)
        }
      }
    }
  }
}