package de.flapdoodle.tab.graph.nodes.connections

import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Input
import de.flapdoodle.tab.graph.events.IsMarker

sealed class In<T: Any> : IsMarker {
  abstract val id: NodeId<out ConnectableNode>
  abstract val variable: Input<T>

  data class Value<T: Any>(
      override val id: NodeId<out ConnectableNode>,
      override val variable: Input.Variable<T>
  ): In<T>()
  data class List<T: Any>(
      override val id: NodeId<out ConnectableNode>,
      override val variable: Input.List<T>
  ): In<T>()
}