package de.flapdoodle.tab.app.model.graph

import de.flapdoodle.graph.GraphAsDot
import de.flapdoodle.graph.Graphs
import de.flapdoodle.graph.VerticesAndEdges
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.calculations.Variable
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.Column
import de.flapdoodle.tab.app.model.data.Data
import de.flapdoodle.tab.app.model.data.SingleValue
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
        val node = model.node(vertex.node)
        return if (node is Node.Calculated<*>) {
            update(vertex, node, model)
        } else {
            model
        }
    }

    private fun <K : Comparable<K>> update(
        vertex: Vertex,
        node: Node.Calculated<K>,
        model: Tab2Model
    ): Tab2Model {
        return update(model, node, when (vertex) {
                is Vertex.Column<*> -> node.calculations.tabular(vertex.columnId)
                is Vertex.SingleValue -> node.calculations.aggregation(vertex.valueId)
            }
        )
    }

    private fun <K : Comparable<K>> update(
        model: Tab2Model,
        node: Node.Calculated<K>,
        calculation: Calculation<K>
    ): Tab2Model {
        val sourceVariables = calculation.variables()
        val neededInputs = node.calculations.inputSlots(sourceVariables)
        val missingSources = neededInputs.filter { it.source == null }
        if (missingSources.isEmpty()) {
            val sources = neededInputs.associateBy { it.source!! }
            val input2data = sources.map { (source, input) ->
                input to dataOf(source, model)
            }
            val variableDataMap = input2data.flatMap { (input, data) ->
                input.mapTo.map { v -> v to data }
            }.toMap()
            return calculate(model, node, calculation, variableDataMap)
        } else {
            println("missing sources: $missingSources")
        }

        return model
    }

    private fun dataOf(
        source: Source,
        model: Tab2Model
    ): Data {
        val node = model.node(source.node)
        val data = when (source) {
            is Source.ColumnSource<*> -> {
                when (node) {
                    is Node.HasColumns<*> -> node.column(source.columnId)
                    else -> {
                        throw IllegalArgumentException("mismatch")
                    }
                }
            }

            is Source.ValueSource -> {
                when (node) {
                    is Node.HasValues -> node.value(source.valueId)
                    else -> {
                        throw IllegalArgumentException("mismatch")
                    }
                }
            }
        }
        return data
    }

    private fun <K : Comparable<K>> calculate(
        model: Tab2Model,
        node: Node.Calculated<K>,
        calculation: Calculation<K>,
        variableDataMap: Map<Variable, Data>
    ): Tab2Model {
        return when (calculation) {
            is Calculation.Aggregation<K> -> calculateAggregate(model, node, calculation, variableDataMap)
            is Calculation.Tabular<K> -> calculateTabular(model, node, calculation, variableDataMap)
        }
    }

    private fun <K : Comparable<K>> calculateAggregate(
        model: Tab2Model,
        node: Node.Calculated<K>,
        calculation: Calculation.Aggregation<K>,
        variableDataMap: Map<Variable, Data>
    ): Tab2Model {
        val valueMap: Map<Variable, Any?> = variableDataMap.map { (v, data) ->
            v to when (data) {
                is SingleValue<*> -> data.value
                else -> throw IllegalArgumentException("not implemented: $data")
            }
        }.toMap()
        val result = calculation.evaluate(valueMap)
        val changedNode: Node.Calculated<K> = setValue(node, calculation, result)
        return model.copy(nodes = model.nodes.map { if (it.id == changedNode.id) changedNode else it })
    }

    private fun <K : Comparable<K>> calculateTabular(
        updated: Tab2Model,
        node: Node.Calculated<K>,
        calculation: Calculation.Tabular<K>,
        variableDataMap: Map<Variable, Data>
    ): Tab2Model {
        val (columns, values) = variableDataMap.entries.partition { it.value is Column<*, *> }
        val columns2var = columns.map { it.value as Column<K, Any> to it.key }
        val singleValueMap = values.map { it.key to (it.value as SingleValue<Any>).value }
        return calculateTabular(updated, node, calculation, columns2var, singleValueMap)
    }

    private fun <K : Comparable<K>> calculateTabular(
        updated: Tab2Model,
        node: Node.Calculated<K>,
        calculation: Calculation.Tabular<K>,
        columns2var: List<Pair<Column<K, Any>, Variable>>,
        singleValueMap: List<Pair<Variable, Any?>>
    ): Tab2Model {
        val interpolated = sortAndInterpolate(columns2var)
        val result = interpolated.index.mapNotNull {
            val result = calculation.evaluate(interpolated.variablesAt(it) + singleValueMap.toMap())
            if (result != null) it to result else null
        }.toMap()

        val changedNode: Node.Calculated<K> = setTable(node, calculation, result)
        return updated.copy(nodes = updated.nodes.map { if (it.id == changedNode.id) changedNode else it })
    }

    private fun <K : Comparable<K>> sortAndInterpolate(columns: List<Pair<Column<K, Any>, Variable>>): InterpolatedColumns<K> {
        val index = columns.flatMap { it.first.index() }.toSet()
        val map2vars = columns.map { Interpolator.valueAtOffset(it.first.values).interpolatedAt(index) to it.second }
        return InterpolatedColumns(index, map2vars)
    }

    data class InterpolatedColumns<K : Any>(
        val index: Set<K>,
        val map2vars: List<Pair<Map<K, Any?>, Variable>>
    ) {
        fun variablesAt(index: K): Map<Variable, Any?> {
            return map2vars.map { it.second to it.first[index] }.toMap()
        }
    }

    private fun <K : Comparable<K>> setValue(
        node: Node.Calculated<K>,
        calculation: Calculation.Aggregation<K>,
        result: Any?
    ): Node.Calculated<K> {
        val changedNode = if (node.values.find(calculation.destination()) == null) {
            val newSingleValue = if (result != null) {
                SingleValue.of(calculation.name(), result, calculation.destination())
            } else {
                SingleValue.ofNull(calculation.name(), calculation.destination())
            }
            node.copy(values = node.values.addValue(newSingleValue))
        } else {
            node.copy(values = node.values.change(calculation.destination()) { old ->
                if (result != null) {
                    SingleValue.of(old.name, result, old.id)
                } else {
                    old.copy(value = null)
                }
            })
        }
        return changedNode
    }

    private fun <K : Comparable<K>> setTable(
        node: Node.Calculated<K>,
        calculation: Calculation.Tabular<K>,
        result: Map<K, Any>
    ): Node.Calculated<K> {
        // TODO multiple value types in result
        val newColumn = column(result, calculation)
        val existingColumn = node.columns.find(calculation.destination())

        val changedNode = if (existingColumn == null) {
            node.copy(columns = node.columns.addColumn(newColumn))
        } else {
            if (existingColumn.valueType != newColumn.valueType) {
                node.copy(columns = node.columns.change(calculation.destination()) { old ->
                    newColumn
                })
            } else {
                node.copy(columns = node.columns.change(calculation.destination()) { old ->
                    newColumn.copy(id = old.id)
                })
            }
        }
        return changedNode
    }

    private fun <K : Comparable<K>> column(
        result: Map<K, Any>,
        calculation: Calculation.Tabular<K>
    ): Column<K, out Any> {
        val valueTypes = result.values.map { it::class }.toSet()
        require(valueTypes.size == 1) { "more than one value type: $result"}
        val valueType = valueTypes.toList().one { true }

        val column = Column(
            calculation.name(),
            calculation.destination().indexType,
            valueType,
            emptyMap(),
            calculation.destination()
        )
        return column.set(result)
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

                        is Node.Calculated<out Comparable<*>> -> {
                            node.calculations.forEach { calculation ->
                                when (calculation) {
                                    is Calculation.Tabular -> {
                                        builder.addVertex(Vertex.Column(node.id, calculation.destination()))
                                    }

                                    is Calculation.Aggregation -> {
                                        builder.addVertex(Vertex.SingleValue(node.id, calculation.destination()))
                                    }
                                }
                            }
                            node.calculations.inputs().forEach { input ->
                                when (input.source) {
                                    is Source.ColumnSource<*> -> {
                                        val sourceVertex = Vertex.Column(input.source.node, input.source.columnId)
                                        builder.addVertex(sourceVertex)
                                        node.calculations.forEach { c ->
                                            val destVertex = when (c) {
                                                is Calculation.Tabular -> {
                                                    Vertex.Column(node.id, c.destination())
                                                }

                                                is Calculation.Aggregation -> {
                                                    Vertex.SingleValue(node.id, c.destination())
                                                }
                                            }
                                            builder.addVertex(destVertex)
                                            builder.addEdge(sourceVertex, destVertex)
                                        }
                                    }

                                    is Source.ValueSource -> {
                                        val sourceVertex = Vertex.SingleValue(input.source.node, input.source.valueId)
                                        builder.addVertex(sourceVertex)
                                        node.calculations.forEach { c ->
                                            val destVertex = when (c) {
                                                is Calculation.Tabular -> {
                                                    Vertex.Column(node.id, c.destination())
                                                }

                                                is Calculation.Aggregation -> {
                                                    Vertex.SingleValue(node.id, c.destination())
                                                }
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