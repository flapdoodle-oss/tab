package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.Model
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.Calculation
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Variable
import tornadofx.*
import java.math.BigDecimal

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
  }
}

