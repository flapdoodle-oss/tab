package de.flapdoodle.tab.data.calculation

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.Nodes
import de.flapdoodle.tab.data.calculations.VariableMap
import de.flapdoodle.tab.data.graph.ColumnGraph
import de.flapdoodle.tab.data.nodes.ConnectableNode

object Calculation {
  fun calculate(nodes: Nodes, data: Data): Data {
    var currentData = data

    val graph = ColumnGraph.of(nodes)
    val columns = graph.columnsStartToEnd()
    val nodeOfColumns = nodes.nodes().flatMap { node ->
      when (node) {
        is ConnectableNode.Table -> node.columns().map { it.id to node }
        is ConnectableNode.Calculated -> node.calculations().map { it.column.id to node }
      }
    }.toMap()

    columns.forEach {id ->
      when (val node = nodeOfColumns[id]) {
        is ConnectableNode.Calculated -> {
          val connections = nodes.connections(node.id)?.variableMappings ?: emptyList()
          val variableMap = VariableMap.variableMap(currentData, connections)

          currentData = node.calculations()
              .find { it.column.id==id }
              ?.calculate(currentData,variableMap) ?: currentData
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