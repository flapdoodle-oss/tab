package de.flapdoodle.tab.data

import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.VariableMapping
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.Connection
import de.flapdoodle.tab.data.nodes.Connections
import de.flapdoodle.tab.data.nodes.HasColumns
import de.flapdoodle.tab.data.nodes.HasInputs
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Variable
import de.flapdoodle.tab.extensions.change

data class Model(
    private val nodes: Map<NodeId<out ConnectableNode>, ConnectableNode> = linkedMapOf(),
    private val connections: Map<NodeId<out ConnectableNode>, Connections> = emptyMap()
) {

  private val connectionMap: Map<NodeId<out ConnectableNode>, List<Connection<out Any>>> = allConnections(nodes, connections)

  fun add(node: ConnectableNode): Model {
    require(!nodes.contains(node.id)) { "node already set: ${node.id}" }
    return copy(nodes = nodes + (node.id to node))
  }

  fun <T : Any> connect(id: NodeId<out ConnectableNode>, variable: Variable<T>, columnConnection: ColumnConnection<T>): Model {
    return copy(connections = connections + (id to (connections[id] ?: Connections()).add(variable, columnConnection)))
  }

  fun <T : ConnectableNode> changeNode(id: NodeId<T>, change: (T) -> ConnectableNode): Model {
    require(nodes.contains(id)) { "node $id not found in ${nodes.keys}" }
    val node = nodes[id] as T ?: throw IllegalArgumentException("node not found for $id in ${nodes.keys}")
    val changedNode = change(node)

    val originalColumnIds = if (node is HasColumns) {
      node.columns().map(NamedColumn<out Any>::id).toSet()
    } else emptySet()

    val newColumnIds = if (changedNode is HasColumns) {
      changedNode.columns().map(NamedColumn<out Any>::id).toSet()
    } else emptySet()

    val missingColumns = originalColumnIds-newColumnIds

    val changedNodes = nodes.mapValues {
      if (it.key == id) {
        changedNode
      } else {
        it.value
      }
    }

    val changedConnections = connections.mapValues { (nodeId, connections) ->
      if (nodeId == id && changedNode is HasInputs)
        connections.filterInvalidInputs(changedNode)
      else
        connections.filterInvalidColumns(missingColumns)
    }

    return copy(nodes = changedNodes, connections = changedConnections)
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
          sourceNode = columnToTableMap[mapping.columnConnection.columnId]
              ?: throw IllegalArgumentException("source not found: $mapping"),
          columnConnection = mapping.columnConnection,
          variable = mapping.variable
      )
    }
  }
}