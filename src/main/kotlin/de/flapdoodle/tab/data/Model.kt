package de.flapdoodle.tab.data

import de.flapdoodle.tab.data.nodes.VariableMapping
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.Connection
import de.flapdoodle.tab.data.nodes.Connections
import de.flapdoodle.tab.data.nodes.HasColumns
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Variable

data class Model(
    private val nodes: Map<NodeId<out ConnectableNode>, ConnectableNode> = linkedMapOf(),
    private val connections: Map<NodeId<out ConnectableNode>, Connections> = emptyMap(),
    private val connectionMap: Map<NodeId<out ConnectableNode>, List<Connection<out Any>>> = allConnections(nodes, connections)
) {

  fun add(node: ConnectableNode): Model {
    require(!nodes.contains(node.id)) { "node already set: ${node.id}" }
    return copy(nodes = nodes + (node.id to node))
  }

  fun <T: Any> connect(id: NodeId<out ConnectableNode>, variable: Variable<T>, columnId: ColumnId<T>): Model {
    return copy(connections = connections + (id to (connections[id] ?: Connections()).add(variable,columnId)))
  }

  fun <T : ConnectableNode> node(id: NodeId<T>): T {
    @Suppress("UNCHECKED_CAST")
    val table = nodes[id] as T?
    require(table != null) { "no node found for $id" }
    return table
  }

  fun nodeIds(): Set<NodeId<out ConnectableNode>> {
    return nodes.keys
  }

  fun nodes(): Collection<ConnectableNode> {
    return nodes.values
  }

  fun connections(id: NodeId<out ConnectableNode>): Connections? {
    return connections[id]
  }

  fun tableConnections(id: NodeId<out ConnectableNode>): List<Connection<out Any>> {
    return connectionMap[id] ?: emptyList()
  }

  companion object {
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
          sourceNode = columnToTableMap[mapping.columnId]
              ?: throw IllegalArgumentException("source not found: $mapping"),
          columnColumn = mapping.columnId,
          variable = mapping.variable
      )
    }
  }
}