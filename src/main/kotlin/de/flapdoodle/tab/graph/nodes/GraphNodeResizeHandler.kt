package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.graph.events.IsMarker
import de.flapdoodle.tab.graph.events.MappedMouseEvent
import de.flapdoodle.tab.graph.events.MouseEventHandler
import de.flapdoodle.tab.graph.events.MouseEventHandlerResolver
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D

class GraphNodeResizeHandler(
    private val resizeMarker: Resize
) : MouseEventHandler {
  var dragStarted: Dimension2D? = null
  var exited: Boolean = false

  override fun onEvent(mouseEvent: MappedMouseEvent, marker: IsMarker?): MouseEventHandler? {
//    println("$mouseEvent -> $marker")
    when (mouseEvent) {
      is MappedMouseEvent.Click -> dragStarted = resizeMarker.parent.size()
      is MappedMouseEvent.Drag -> dragStarted?.let { it + mouseEvent.delta }?.apply {
//        println("should move ${resizeMarker.parent} by ${mouseEvent.delta}")
        resizeMarker.parent.resizeTo(this.width, this.height)
      }
      is MappedMouseEvent.Release -> dragStarted = null
      is MappedMouseEvent.Exit -> exited = exited || marker == resizeMarker
        else -> {
            
        }
    }

    return if (exited && dragStarted == null) {
//      println("exit $resizeMarker because no drag in progress")
      null
    } else
      this
  }

  companion object {
//      val resolver = MouseEventHandlerResolver.forType(::GraphNodeResizeHandler)
      val resolver = MouseEventHandlerResolver.forInstance(Resize::class.java, ::GraphNodeResizeHandler)
  }

}

private operator fun Dimension2D.plus(delta: Point2D): Dimension2D {
  return Dimension2D(this.width + delta.x, this.height + delta.y)
}
