package de.flapdoodle.tab.controls.tables

import javafx.event.Event
import javafx.event.EventType

sealed class SmartEvents(eventType: EventType<SmartEvents>) : Event(eventType) {
  companion object {
    val ALL = EventType<SmartEvents>(Event.ANY, "ALL")
    val EditDoneEvent = EventType(ALL, "DONE")

    val TABLE = EventType(ALL, "TABLE")
    val CELL = EventType(ALL, "CELL")
  }

  data class EditDone(val cell: SmartCell<out Any, out Any>) : SmartEvents(EditDoneEvent)

  data class ChangeCursor<T: Any>(val cursor: Cursor<T>) : SmartEvents(TABLE)
  data class MoveCursor(val deltaRow: Int = 0, val deltaColumn: Int = 0) : SmartEvents(TABLE)

  data class SetCursor<T: Any>(val cursor: Cursor<T>): SmartEvents(CELL)
  data class CellFocused(val cell: SmartCell<out Any, out Any>) : SmartEvents(CELL)
}