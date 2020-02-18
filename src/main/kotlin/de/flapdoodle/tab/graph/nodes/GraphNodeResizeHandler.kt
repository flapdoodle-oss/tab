package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.graph.events.IsMarker
import de.flapdoodle.tab.graph.events.MappedMouseEvent
import de.flapdoodle.tab.graph.events.MouseEventHandler
import de.flapdoodle.tab.graph.events.MouseEventHandlerResolver
import javafx.geometry.Point2D
import tornadofx.*

class GraphNodeResizeHandler(
    private val resizeMarker: Resize
) : MouseEventHandler {
  var dragStarted: Point2D? = null
  var exited: Boolean = false

  override fun onEvent(mouseEvent: MappedMouseEvent, marker: IsMarker?): MouseEventHandler? {
//    println("$mouseEvent -> $marker")
    when (mouseEvent) {
      is MappedMouseEvent.Click -> dragStarted = resizeMarker.parent.size()
      is MappedMouseEvent.Drag -> dragStarted?.let { it + mouseEvent.delta }?.apply {
//        println("should move ${resizeMarker.parent} by ${mouseEvent.delta}")
        resizeMarker.parent.resizeTo(this.x, this.y)
      }
      is MappedMouseEvent.Release -> dragStarted = null
      is MappedMouseEvent.Exit -> exited = exited || marker == resizeMarker
    }

    return if (exited && dragStarted == null) {
//      println("exit $resizeMarker because no drag in progress")
      null
    } else
      this
  }

  companion object {
    val resolver = MouseEventHandlerResolver.forType(::GraphNodeResizeHandler)
  }

}