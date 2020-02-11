package de.flapdoodle.tab.graph.events2

import javafx.event.EventTarget

interface MouseEventHandler {
  fun onEnter(eventTarget: EventTarget): MouseEventHandler? = this
  fun onExit(eventTarget: EventTarget): MouseEventHandler? = this
}