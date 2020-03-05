package de.flapdoodle.tab.graph.events

import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent


sealed class MappedMouseEvent(
    internal val event: MouseEvent
) {

  class Enter(event: MouseEvent) : MappedMouseEvent(event)
  class Click(event: MouseEvent, val coord: Point2D) : MappedMouseEvent(event)
  class Move(event: MouseEvent, val coord: Point2D) : MappedMouseEvent(event)
  class DragDetected(event: MouseEvent, val delta: Point2D) : MappedMouseEvent(event) {
    fun startFullDrag() {
      (event.target as Node).startFullDrag()
    }
  }
  class Drag(event: MouseEvent, val delta: Point2D, val coord: Point2D) : MappedMouseEvent(event)
  class DragEnter(event: MouseEvent, val delta: Point2D) : MappedMouseEvent(event)
  class DragExit(event: MouseEvent, val delta: Point2D) : MappedMouseEvent(event)
  class DragRelease(event: MouseEvent, val delta: Point2D) : MappedMouseEvent(event)

  class Release(event: MouseEvent, val delta: Point2D) : MappedMouseEvent(event)
  class Exit(event: MouseEvent) : MappedMouseEvent(event)
}