package de.flapdoodle.tab.graph.nodes.renderer.events

import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Variable
import de.flapdoodle.tab.graph.nodes.connections.VariableInput
import tornadofx.*

data class ConnectEvent(
  val data: EventData
) : FXEvent() {

  sealed class EventData {
    fun asEvent(): ConnectEvent {
      return ConnectEvent(this)
    }

    data class StartConnectTo<T : Any>(
        val id: NodeId<out ConnectableNode>,
        val variable: Variable<T>
    ) : EventData()

    object StopConnect: EventData()
  }

  companion object {
    fun <T: Any> startConnectTo(dest: VariableInput<T>): ConnectEvent {
      return EventData.StartConnectTo(dest.id, dest.variable).asEvent()
    }

    fun stop(): ConnectEvent {
      return EventData.StopConnect.asEvent()
    }
  }
}
