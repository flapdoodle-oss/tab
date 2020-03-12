package de.flapdoodle.tab.graph.nodes.renderer.events

import de.flapdoodle.tab.data.ColumnId
import tornadofx.*

data class ExplainEvent(
  val data: EventData
) : FXEvent() {

  sealed class EventData {
    fun asEvent(): ExplainEvent {
      return ExplainEvent(this)
    }

    data class ColumnSelected<T : Any>(
        val id: ColumnId<T>
    ) : EventData()

    object NoColumnSelected: EventData()
  }

  companion object {
    fun <T: Any> columnselected(id: ColumnId<T>): ExplainEvent {
      return EventData.ColumnSelected(id).asEvent()
    }

    fun noColumnSelected(): ExplainEvent {
      return EventData.NoColumnSelected.asEvent()
    }
  }
}
