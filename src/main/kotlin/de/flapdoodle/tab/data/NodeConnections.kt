package de.flapdoodle.tab.data

import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.Connections
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Variable

data class NodeConnections(
    internal val connections: Map<NodeId<out ConnectableNode>, Connections> = emptyMap()
) {

  fun <T : Any> connect(id: NodeId<out ConnectableNode>, variable: Variable<T>, columnConnection: ColumnConnection<T>): NodeConnections {
    return copy(connections = connections + (id to (connections[id] ?: Connections()).add(variable, columnConnection)))
  }


  fun connections(id: NodeId<out ConnectableNode>): Connections? {
    return connections[id]
  }

  fun filterRemoved(nodes: Nodes): NodeConnections {
    println("-----------------------------")
    println("before:")
    explain()
    val ret = filterNodeIds(nodes.nodeIds()).apply {
          println("after filterNodeIds: ")
          println("-> ${nodes.nodeIds()}")
          explain()
        }
        .filterColumns(nodes.allColumnIds()).apply {
          println("after filterColumns")
          println("-> ${nodes.allColumnIds()}")
          explain()
        }
        .filterInputs(nodes.allInputs()).apply {
          println("after filterInputs")
          explain()
        }
    println("-----------------------------")
    return ret
  }

  private fun filterNodeIds(nodeIds: Collection<NodeId<out ConnectableNode>>): NodeConnections {
    return if (!nodeIds.containsAll(connections.keys)) {
      val filteredConnections = connections.filter { nodeIds.contains(it.key) }
      copy(connections = filteredConnections)
    } else this
  }

  private fun filterColumns(columnIds: Set<ColumnId<out Any>>): NodeConnections {
    val changed = connections.mapValues {
      it.value.filterColumns(columnIds)
    }
    if (changed != connections) {
      return copy(connections = changed)
    }
    return this
  }

  private fun filterInputs(allInputs: Map<NodeId<out ConnectableNode>, Set<Variable<out Any>>>): NodeConnections {
    val changed = connections.mapValues {
      val allVariables = allInputs[it.key]
      require(allVariables!=null) { "is null"}
      it.value.filterInputs(allVariables)
    }
    if (changed != connections) {
      return copy(connections = changed)
    }
    return this
  }

  fun explain() {
    connections.forEach { nodeId, connections ->
      println("node $nodeId -> $connections")
    }
  }
}