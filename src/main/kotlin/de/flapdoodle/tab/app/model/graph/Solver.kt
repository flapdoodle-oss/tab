package de.flapdoodle.tab.app.model.graph

import de.flapdoodle.graph.GraphAsDot
import de.flapdoodle.graph.Graphs
import de.flapdoodle.graph.VerticesAndEdges
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.calculations.InputSlot
import de.flapdoodle.tab.app.model.calculations.Variable
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.*
import de.flapdoodle.tab.types.one
import org.jgrapht.graph.DefaultEdge

object Solver {

    fun solve(model: Tab2Model): Tab2Model {
        val roots = verticesAndEdges(model)
        var updated = model
        roots.forEach { ve ->
            updated = update(updated, ve)
        }
        return updated
    }

    private fun update(model: Tab2Model, ve: VerticesAndEdges<Vertex, DefaultEdge>): Tab2Model {
        var updated = model
        if (ve.loops().isEmpty()) {
            ve.vertices().forEach { updated = update(updated, it) }
        } else {
            throw IllegalArgumentException("loops: ${ve.loops()}")
        }
        return updated
    }

    private fun update(model: Tab2Model, vertex: Vertex): Tab2Model {
        var updated = model
        when (val node = model.node(vertex.node)) {
            is Node.Calculated<*> -> {
                val matching = when (vertex) {
                    is Vertex.Column<*> -> {
                        node.calculations.list.one { c ->
                            c is Calculation.Tabular<*,*> && c.destination == vertex.columnId
                        }
                    }
                    is Vertex.SingleValue -> {
                        node.calculations.list.one { c ->
                            c is Calculation.Aggregation && c.destination == vertex.valueId
                        }
                    }
                }

                updated = update(updated, node, matching)
            }
            else -> {
                println("can skip $node")
            }
        }
        return updated
    }

    private fun <K: Comparable<K>> update(model: Tab2Model, node: Node.Calculated<K>, calculation: Calculation): Tab2Model {
        var updated = model
        val sourceVariables = calculation.formula.variables()
        val neededInputs = node.calculations.inputs.filter { it.mapTo.intersect(sourceVariables).isNotEmpty() }
        val missingSources = neededInputs.filter { it.source==null }
        if (missingSources.isEmpty()) {
            val sources = neededInputs.associateBy { it.source!! }
            val input2data = sources.map { (source, input) ->
                val data = when (source) {
                    is Source.ColumnSource<*> -> {
                        updated.node(source.node).data(source.columnId)
                    }
                    is Source.ValueSource -> {
                        updated.node(source.node).data(source.valueId)
                    }
                }
                input to data
            }
            val variableDataMap = input2data.flatMap { (input, data) ->
                input.mapTo.map { v -> v to data }
            }.toMap()
            updated = calculate(updated, node, calculation, variableDataMap)
        } else {
            println("missing sources: $missingSources")
        }

        return updated
    }

    private fun <K: Comparable<K>> calculate(
        model: Tab2Model,
        node: Node.Calculated<K>,
        calculation: Calculation,
        variableDataMap: Map<Variable, Data>
    ): Tab2Model {
//        println("calculate ${calculation.formula} with $variableDataMap")
        var updated = model
        when (calculation) {
            is Calculation.Aggregation -> {
                val valueMap: Map<Variable, Any?> = variableDataMap.map { (v, data) ->
                    v to when (data) {
                        is SingleValue<*> -> data.value
                        else -> throw IllegalArgumentException("not implemented: $data")
                    }
                }.toMap()
                val result = calculation.formula.evaluate(valueMap)
//                println("--> $result")
                val changedNode: Node.Calculated<K> = setValue(node, calculation, result)

//                val changedNode = if (result != null)
//                    node.copy(values = node.values.changeWithType(calculation.destination, result))
//                else
//                    node.copy(values = node.values.change(calculation.destination, null))
                updated = updated.copy(nodes = model.nodes.map { if (it.id == changedNode.id) changedNode else it  })
            }
            else -> {
                throw IllegalArgumentException("not implemented")
            }
        }
        return updated
    }

    private fun <K: Comparable<K>> setValue(
        node: Node.Calculated<K>,
        calculation: Calculation.Aggregation,
        result: Any?
    ): Node.Calculated<K> {
        val changedNode = if (node.values.find(calculation.destination)==null) {
            val newSingleValue = if (result!=null) {
                SingleValue(calculation.name, result::class, result, calculation.destination)
            } else {
                SingleValue(calculation.name, Unit::class, result, calculation.destination)
            }
            node.copy(values = node.values.addValue(newSingleValue))
        } else {
            node.copy(values = node.values.change(calculation.destination) { old ->
                if (result!=null) {
                    SingleValue(old.name, result::class, result, old.id)
                } else {
                    old.copy(value = null)
                }
            })
        }
        return changedNode
    }


    private fun verticesAndEdges(
        model: Tab2Model
    ): Collection<VerticesAndEdges<Vertex, DefaultEdge>> {
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
                                    is Calculation.Tabular<*, *> -> {
                                        builder.addVertex(Vertex.Column(node.id, calculation.destination))
                                    }

                                    is Calculation.Aggregation -> {
                                        builder.addVertex(Vertex.SingleValue(node.id, calculation.destination))
                                    }
                                }
                            }
                            node.calculations.inputs.forEach { input ->
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

        return Graphs.rootsOf(graph)
    }
}