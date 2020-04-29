package de.flapdoodle.tab.graph.nodes.renderer.events

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Data
import tornadofx.*

data class DataEvent(
    val data: EventData
) : FXEvent() {

  sealed class EventData {
    fun asEvent(): DataEvent {
      return DataEvent(this)
    }

    abstract fun applyTo(data: Data): Data

    data class Changed<T : Any>(
        val id: ColumnId<out T>,
        val row: Int,
        val value: T?
    ) : EventData() {
      override fun applyTo(data: Data): Data {
        return data.change(id, row, value)
      }
    }

    data class ValueChanged<T : Any>(
        val id: ColumnId<out T>,
        val value: T?
    ) : EventData() {
      override fun applyTo(data: Data): Data {
        return data.change(id, value)
      }
    }
  }
}

