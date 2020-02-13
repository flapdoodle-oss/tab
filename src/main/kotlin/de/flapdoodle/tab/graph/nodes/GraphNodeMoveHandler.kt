package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.graph.events.IsMarker
import de.flapdoodle.tab.graph.events.MappedMouseEvent
import de.flapdoodle.tab.graph.events.MouseEventHandler
import de.flapdoodle.tab.graph.events.MouseEventHandlerResolver
import javafx.geometry.Point2D
import tornadofx.*

class GraphNodeMoveHandler(
    private val moveMarker: AbstractGraphNode.Move
) : MouseEventHandler{
  var dragStarted: Point2D? = null
  var exited: Boolean = false

  override fun onEvent(mouseEvent: MappedMouseEvent, marker: IsMarker?): MouseEventHandler? {
//    println("$mouseEvent -> $marker")
    when (mouseEvent) {
      is MappedMouseEvent.Click -> dragStarted = moveMarker.parent.position()
      is MappedMouseEvent.Drag -> dragStarted?.let { it + mouseEvent.delta }?.apply {
//        println("should move ${moveMarker.parent} by ${mouseEvent.delta}")
        moveMarker.parent.moveTo(this.x, this.y)
      }
      is MappedMouseEvent.Release -> dragStarted = null
      is MappedMouseEvent.Exit -> exited = exited || marker == moveMarker
    }

    return if (exited && dragStarted == null) {
//      println("exit $moveMarker because no drag in progress")
      null
    } else
      this
  }

  companion object {
    val resolver = MouseEventHandlerResolver.forType(::GraphNodeMoveHandler)
  }
}