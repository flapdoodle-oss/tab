package de.flapdoodle.tab.graph.nodes.renderer.events

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Variable
import de.flapdoodle.tab.graph.nodes.connections.VariableInput
import javafx.geometry.Point2D
import tornadofx.*

data class ConnectEvent(
    val data: EventData
) : FXEvent() {

  companion object {
    fun <T : Any> startConnectTo(dest: VariableInput<T>, start: Point2D): ConnectEvent {
      return EventData.StartConnectTo(dest.id, dest.variable, start).asEvent()
    }

    fun <T : Any> connectTo(dest: VariableInput<T>, end: Point2D, source: ColumnId<T>?): ConnectEvent {
      return EventData.ConntectTo(dest.id, dest.variable, end, source).asEvent()
    }

    fun <T : Any> startConnectFrom(source: ColumnId<T>, start: Point2D): ConnectEvent {
      return EventData.StartConnectFrom(source, start).asEvent()
    }

    fun <T : Any> connectFrom(source: ColumnId<T>, end: Point2D, dest: VariableInput<T>?): ConnectEvent {
      return EventData.ConntectFrom(source, end, dest?.id, dest?.variable).asEvent()
    }

    fun stop(): ConnectEvent {
      return EventData.StopConnect.asEvent()
    }
  }

  sealed class EventData {
    fun asEvent(): ConnectEvent {
      return ConnectEvent(this)
    }

    data class StartConnectTo<T : Any>(
        val id: NodeId<out ConnectableNode>,
        val variable: Variable<T>,
        val toCoord: Point2D
    ) : EventData()

    data class ConntectTo<T: Any>(
        val id: NodeId<out ConnectableNode>,
        val variable: Variable<T>,
        val fromCoord: Point2D,
        val source: ColumnId<T>?
    ) : EventData()

    data class StartConnectFrom<T : Any>(
        val source: ColumnId<T>,
        val fromCoord: Point2D
    ) : EventData()

    data class ConntectFrom<T: Any>(
        val source: ColumnId<T>,
        val toCoord: Point2D,
        val id: NodeId<out ConnectableNode>?,
        val variable: Variable<T>?
    ) : EventData()

    object StopConnect : EventData()
  }
}
