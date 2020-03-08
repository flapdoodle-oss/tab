package de.flapdoodle.tab.data.graph

import de.flapdoodle.graph.GraphAsDot
import de.flapdoodle.graph.Graphs
import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.NodeConnections
import de.flapdoodle.tab.data.Nodes
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.Connections
import de.flapdoodle.tab.data.values.Variable
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

data class ColumnGraph(
    private val graph: DefaultDirectedGraph<ColumnId<out Any>, DefaultEdge>
) {
  init {

    val dotContent = GraphAsDot.builder<ColumnId<out Any>> { it.type.simpleName + "-" + it.id }
        .nodeAttributes { mapOf("label" to it.toString()) }
        .label("columngraph")
        .build()
        .asDot(graph)

    println("-----------------")
    println(dotContent)
    println("-----------------")
    val loops = Graphs.loopsOf(graph)

    loops.forEach {
      println("loop: $it")
    }

//    val roots = Graphs.rootsOf(graph)


  }

  fun columnsStartToEnd(): List<ColumnId<out Any>> {
    val loops = Graphs.loopsOf(graph)
    require(loops.isEmpty()) { "graph contains loops" }

    val roots = Graphs.rootsOf(graph)
    return roots.flatMap { it.vertices() }
  }

  fun possibleDestinationsFor(id: ColumnId<out Any>): Set<ColumnId<out Any>> {
    val allOther = graph.vertexSet()
    return (allOther - id).filter {
      !Graphs.hasPath(graph, it, id)
    }.toSet()
  }

  fun possibleSourcesFor(id: ColumnId<out Any>): Set<ColumnId<out Any>> {
    val allOther = graph.vertexSet()
    return (allOther - id).filter {
      !Graphs.hasPath(graph, id, it)
    }.toSet()
  }

  companion object {

    fun of(nodes: Nodes, nodeConnections: NodeConnections): ColumnGraph {
      val wrapper = Wrapper()
      nodes.nodeIds().forEach { id ->
        val connections = nodeConnections.connections(id)
        if (connections != null) {
          val node = nodes.node(id)
          when (node) {
            is ConnectableNode.Calculated -> {
              node.calculations().forEach { calculation ->
                val dest = calculation.column.id
                val mappedSources = calculation.calculation.variables().mapNotNull {
                  mappedSource(connections, it)
                }
                wrapper.add(dest, mappedSources)
              }
            }
            else -> throw IllegalArgumentException("not implemented: $node")
          }
        }
      }
      return ColumnGraph(wrapper.build())
    }

    private fun mappedSource(connections: Connections, variable: Variable<out Any>): ColumnId<out Any>? {
      return connections.variableMappings.find { it.variable == variable }?.columnConnection?.columnId
    }

    class Wrapper {
      private val builder = Graphs.graphBuilder(Graphs.directedGraph<ColumnId<out Any>>()).get()

      fun add(dest: ColumnId<out Any>, mappedSources: List<ColumnId<out Any>>) {
        builder.addVertex(dest)
        mappedSources.forEach {
          builder.addVertex(it)
          builder.addEdge(it, dest)
        }
      }

      fun build(): DefaultDirectedGraph<ColumnId<out Any>, DefaultEdge> {
        return builder.build()
      }
    }
  }
}