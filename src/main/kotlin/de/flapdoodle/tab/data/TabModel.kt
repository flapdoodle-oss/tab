package de.flapdoodle.tab.data

import de.flapdoodle.tab.data.calculation.Calculation
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.Connection
import de.flapdoodle.tab.data.nodes.Connections
import de.flapdoodle.tab.data.nodes.HasColumns
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.nodes.VariableMapping
import de.flapdoodle.tab.data.values.Variable

data class TabModel(
    val nodes: Nodes = Nodes(),
    val nodeConnections: NodeConnections = NodeConnections(),
    val data: Data = Data()
) {

  init {
    val allNodes = nodes.nodes.keys
    val connectedNodes = nodeConnections.connections.keys

    require(allNodes.containsAll(connectedNodes)) {"more connections than nodes: $connectedNodes > $allNodes"}
  }

  private val connectionMap: Map<NodeId<out ConnectableNode>, List<Connection<out Any>>> = allConnections(nodes.nodes, nodeConnections.connections)

  fun applyNodeChanges(change: (Nodes) -> Nodes): TabModel {
    val changed = change(nodes)
    if (changed != nodes) {
      val changedConnections = nodeConnections.filterRemoved(changed)
      return copy(nodes = changed, nodeConnections = changedConnections, data = calculate(changed, changedConnections, data))
    }
    return this
  }

  fun applyConnectionChanges(change: (NodeConnections) -> NodeConnections): TabModel {
    val changed = change(nodeConnections)
    if (changed != nodeConnections) {
      return copy(nodeConnections = changed, data = calculate(nodes, changed, data))
    }
    return this
  }

  fun applyDataChanges(change: (Data) -> Data): TabModel {
    val changed = change(data)
    if (changed != data) {
      return copy(data = calculate(nodes, nodeConnections, changed))
    }
    return this
  }

  fun add(node: ConnectableNode): TabModel {
    return applyNodeChanges { nodes -> nodes.add(node) }
  }

  fun <T : Any> connect(id: NodeId<out ConnectableNode>, variable: Variable<T>, columnConnection: ColumnConnection<T>): TabModel {
    return applyConnectionChanges {
      it.connect(id,variable,columnConnection)
    }
  }

//  private fun updateData(): TabModel {
//    val changed = Calculation.calculate(nodes, nodeConnections, data)
//    if (changed != data) {
//      return copy(data = changed)
//    }
//    return this
//  }

  fun nodeIds(): Set<NodeId<out ConnectableNode>> {
    return nodes.nodeIds()
  }

  fun tableConnections(id: NodeId<out ConnectableNode>): List<Connection<out Any>> {
    return connectionMap[id] ?: emptyList()
  }

  companion object {
    private fun calculate(nodes: Nodes, nodeConnections: NodeConnections, data: Data): Data {
      println("----------------------------")
      println("nodes:")
      nodes.explain()
      println("- - - - - - - - - - - - - - ")
      println("connections:")
      nodeConnections.explain()
      println("- - - - - - - - - - - - - - ")
      println("data:")
      data.explain()
      println("----------------------------")

      val changed = Calculation.calculate(nodes, nodeConnections, data)
      return if (changed != data) changed else data
    }

    private fun allConnections(
        nodes: Map<NodeId<out ConnectableNode>, ConnectableNode>,
        connections: Map<NodeId<out ConnectableNode>, Connections>
    ): Map<NodeId<out ConnectableNode>, List<Connection<out Any>>> {
      val columnToTableMap = nodes.map { (id, node) ->
        when (node) {
          is HasColumns -> id to node.columns().map { it.id }
          else -> id to emptyList()
        }
      }.flatMap { pair ->
        pair.second.map { it to pair.first }
      }.toMap()

      return connections.map { (id, c) ->
        id to c.variableMappings.map {
          connection(it, columnToTableMap)
        }
      }.toMap()
    }

    private fun <T : Any> connection(
        mapping: VariableMapping<T>,
        columnToTableMap: Map<ColumnId<out Any>, NodeId<out ConnectableNode>>
    ): Connection<T> {
      return Connection(
          sourceNode = columnToTableMap[mapping.columnConnection.columnId]
              ?: throw IllegalArgumentException("source not found: $mapping"),
          columnConnection = mapping.columnConnection,
          variable = mapping.variable
      )
    }
  }

}