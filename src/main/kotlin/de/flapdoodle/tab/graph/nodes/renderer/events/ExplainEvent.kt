package de.flapdoodle.tab.graph.nodes.renderer.events

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Variable
import de.flapdoodle.tab.graph.nodes.connections.VariableInput
import javafx.geometry.Point2D
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
