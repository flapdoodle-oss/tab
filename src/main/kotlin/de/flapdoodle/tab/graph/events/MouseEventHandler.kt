package de.flapdoodle.tab.graph.events

fun interface MouseEventHandler {
  fun onEvent(mouseEvent: MappedMouseEvent, marker: IsMarker?): MouseEventHandler?
}