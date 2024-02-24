package de.flapdoodle.tab.app.model.graph

import de.flapdoodle.graph.GraphAsDot
import de.flapdoodle.graph.Graphs
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId
import org.jgrapht.graph.DefaultEdge

object Solver {

    fun solve(model: Tab2Model): Tab2Model {
        val graph = Graphs.with(Graphs.graphBuilder(Graphs.directedGraph(Vertex::class.java, DefaultEdge::class.java)))
            .build { builder ->
                model.nodes.forEach { node ->
                    when (node) {
                        is Node.Constants -> {
                            node.values.forEach { value ->
                                builder.addVertex(Vertex.SingleValue(node.id, value.id))
                            }
                        }
                        is Node.Table<*> -> {
                            node.columns.forEach { column ->
                                builder.addVertex(Vertex.Column(node.id, column.id))
                            }
                        }
                        is Node.Calculated<*> -> {
                            node.calculations.forEach { calculation ->
                                when (calculation) {
                                    is Calculation.Tabular<*,*> -> {
                                        builder.addVertex(Vertex.Column(node.id, calculation.destination))
                                    }
                                    is Calculation.Aggregation -> {
                                        builder.addVertex(Vertex.SingleValue(node.id, calculation.destination))
                                    }
                                }
                            }
                            node.calculations.inputs.forEach {input ->
                                when (input.source) {
                                    is Source.ColumnSource<*> -> {
                                        val sourceVertex = Vertex.Column(input.source.node, input.source.columnId)
                                        builder.addVertex(sourceVertex)
                                        node.calculations.destinations(input)?.forEach { d ->
                                            val destVertex = when (d) {
                                                is ColumnId<*> -> Vertex.Column(node.id, d)
                                                is SingleValueId -> Vertex.SingleValue(node.id, d)
                                            }
                                            builder.addVertex(destVertex)
                                            builder.addEdge(sourceVertex, destVertex)
                                        }
                                    }

                                    is Source.ValueSource -> {
                                        val sourceVertex = Vertex.SingleValue(input.source.node, input.source.valueId)
                                        builder.addVertex(sourceVertex)
                                        node.calculations.destinations(input)?.forEach { d ->
                                            val destVertex = when (d) {
                                                is ColumnId<*> -> Vertex.Column(node.id, d)
                                                is SingleValueId -> Vertex.SingleValue(node.id, d)
                                            }
                                            builder.addVertex(destVertex)
                                            builder.addEdge(sourceVertex, destVertex)
                                        }
                                    }
                                    else -> {
                                        // no source
                                    }
                                }
                            }
                        }
                    }
                }
            }
        val dot = GraphAsDot.builder<Vertex> { it ->
            when (it) {
                is Vertex.Column<*> -> "column(${it.node}:${it.columnId})"
                is Vertex.SingleValue -> "value(${it.node}:${it.valueId})"
            }
        }
            .build().asDot(graph)
        println("----------------------------")
        println(dot)
        return model
    }
}