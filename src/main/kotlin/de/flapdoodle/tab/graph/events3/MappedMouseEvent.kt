package de.flapdoodle.tab.graph.events3

import javafx.geometry.Point2D

sealed class MappedMouseEvent {
  class Enter : MappedMouseEvent()
  data class Click(val coord: Point2D) : MappedMouseEvent()
  class Drag(val delta: Point2D) : MappedMouseEvent()
  class Release(val delta: Point2D) : MappedMouseEvent()
  class Exit : MappedMouseEvent()
}