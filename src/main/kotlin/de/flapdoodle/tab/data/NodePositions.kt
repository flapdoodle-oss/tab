package de.flapdoodle.tab.data

import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D

data class NodePositions(
    var positions: Map<NodeId<out ConnectableNode>, Pair<Point2D, Dimension2D>> = emptyMap()
) {

  fun set(id: NodeId<out ConnectableNode>, position: Point2D, size: Dimension2D): NodePositions {
    return copy(positions = positions + (id to (position to size)))
  }

  fun forEach(action: (NodeId<out ConnectableNode>, Point2D, Dimension2D) -> Unit) {
    positions.forEach {
      action(it.key, it.value.first, it.value.second)
    }
  }
}