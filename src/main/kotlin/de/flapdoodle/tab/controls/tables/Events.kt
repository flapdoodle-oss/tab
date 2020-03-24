package de.flapdoodle.tab.controls.tables

import javafx.event.Event
import javafx.event.EventType

sealed class Events(eventType: EventType<Events>) : Event(eventType) {
  companion object {
    val ALL = EventType<Events>(Event.ANY, "ALL")
    val EditDoneEvent = EventType(ALL, "DONE")

    val TABLE = EventType(ALL, "TABLE")
    val CELL = EventType(ALL, "CELL")
  }

  data class EditDone(val cell: SmartCell<out Any, out Any>) : Events(EditDoneEvent)

  data class ChangeCursor<T: Any>(val cursor: Cursor<T>) : Events(TABLE)
  data class MoveCursor(val deltaRow: Int = 0, val deltaColumn: Int = 0) : Events(TABLE)

  data class CellFocused(val cell: SmartCell<out Any, out Any>) : Events(CELL)
}