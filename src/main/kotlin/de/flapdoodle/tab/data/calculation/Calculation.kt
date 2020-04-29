package de.flapdoodle.tab.data.calculation

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.NodeConnections
import de.flapdoodle.tab.data.Nodes
import de.flapdoodle.tab.data.calculations.ListMap
import de.flapdoodle.tab.data.calculations.VariableMap
import de.flapdoodle.tab.data.graph.ColumnGraph
import de.flapdoodle.tab.data.nodes.ConnectableNode

object Calculation {
  fun calculate(nodes: Nodes, nodeConnections: NodeConnections, data: Data): Data {
    var currentData = data

    val graph = ColumnGraph.of(nodes, nodeConnections)
    val columns = graph.columnsStartToEnd()
    val nodeOfColumns = nodes.nodes().flatMap { node ->
      when (node) {
        is ConnectableNode.Constants -> node.columns().map { it.id to node }
        is ConnectableNode.Table -> node.columns().map { it.id to node }
        is ConnectableNode.Calculated -> node.calculations().map { it.column.id to node }
        is ConnectableNode.Aggregated -> node.columns().map { it.id to node }
      }
    }.toMap()

    columns.forEach {id ->
      when (val node = nodeOfColumns[id]) {
        is ConnectableNode.Calculated -> {
          val connections = nodeConnections.connections(node.id)?.variableMappings ?: emptyList()
          val variableMap = VariableMap.variableMap(currentData, connections)

          currentData = node.calculations()
              .find { it.column.id==id }
              ?.calculate(currentData,variableMap) ?: currentData
        }
        is ConnectableNode.Table -> {
          // data already there
        }
        is ConnectableNode.Constants -> {
          // data already there
        }
        is ConnectableNode.Aggregated -> {
          val connections = nodeConnections.connections(node.id)?.variableMappings ?: emptyList()
          val listMap = ListMap.variableMap(currentData, connections)

          currentData = node.aggregations()
              .find { it.column.id==id }
              ?.aggregate(currentData,listMap) ?: currentData
        }
      }
    }

//    val calcTables = model.nodes().filterIsInstance<ConnectableNode.Calculated>()
//    calcTables.forEach {
//      val connections = model.connections(it.id)?.variableMappings ?: emptyList()
//      val variableMap = VariableMap.variableMap(currentData, connections)
//      currentData = it.calculate(currentData, variableMap)
//    }
    return currentData
  }

}