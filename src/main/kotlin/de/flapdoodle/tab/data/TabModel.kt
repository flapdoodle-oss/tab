package de.flapdoodle.tab.data

import de.flapdoodle.tab.data.calculation.Calculation
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.Connection
import de.flapdoodle.tab.data.nodes.NodeId

data class TabModel(
    val nodes: Nodes = Nodes(),
    val connections: NodeConnections = NodeConnections(),
    val data: Data = Data()
) {

  fun applyNodeChanges(change: (Nodes) -> Nodes): TabModel {
    val changed = change(nodes)
    if (changed != nodes) {
      return copy(nodes = changed).updateData()
    }
    return this
  }

  fun applyDataChanges(change: (Data) -> Data): TabModel {
    val changed = change(data)
    if (changed != data) {
      return copy(data = changed).updateData()
    }
    return this
  }

  private fun updateData(): TabModel {
    val changed = Calculation.calculate(nodes, data)
    if (changed != data) {
      return copy(data = changed)
    }
    return this
  }

  fun nodeIds(): Set<NodeId<out ConnectableNode>> {
    return nodes.nodeIds()
  }

  fun tableConnections(id: NodeId<out ConnectableNode>): List<Connection<out Any>> {
    return nodes.tableConnections(id)
  }
}