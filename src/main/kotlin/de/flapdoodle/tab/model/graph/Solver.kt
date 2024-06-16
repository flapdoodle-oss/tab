package de.flapdoodle.tab.model.graph

import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.eval.core.exceptions.BaseException
import de.flapdoodle.graph.GraphAsDot
import de.flapdoodle.graph.Graphs
import de.flapdoodle.graph.VerticesAndEdges
import de.flapdoodle.kfx.logging.Logging
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Model
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Variable
import de.flapdoodle.tab.model.calculations.interpolation.DefaultInterpolatorFactoryLookup
import de.flapdoodle.tab.model.calculations.interpolation.Interpolator
import de.flapdoodle.tab.model.calculations.types.IndexMap
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.Data
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.types.Unknown
import de.flapdoodle.tab.types.one
import de.flapdoodle.tab.ui.ModelAdapter
import org.jgrapht.graph.DefaultEdge

object Solver {
    private val logger = Logging.logger(Solver::class)

    fun solve(model: Model): Model {
        val roots = verticesAndEdges(model)
        var updated = model
        roots.forEach { ve ->
            updated = update(updated, ve)
        }
        return updated
    }

    private fun update(model: Model, ve: VerticesAndEdges<Vertex, DefaultEdge>): Model {
        var updated = model
        if (ve.loops().isEmpty()) {
            ve.vertices().forEach { updated = update(updated, it) }
        } else {
            throw IllegalArgumentException("loops: ${ve.loops()}")
        }
        return updated
    }

    private fun update(model: Model, vertex: Vertex): Model {
        val node = model.node(vertex.node)
        return if (node is de.flapdoodle.tab.model.Node.Calculated<*>) {
            update(vertex, node, model)
        } else {
            model
        }
    }

    private fun <K : Comparable<K>> update(
        vertex: Vertex,
        node: de.flapdoodle.tab.model.Node.Calculated<K>,
        model: Model
    ): Model {
        return update(model, node, when (vertex) {
                is Vertex.Column -> node.calculations.tabular(vertex.columnId)
                is Vertex.SingleValue -> node.calculations.aggregation(vertex.valueId)
            }
        )
    }

    private fun <K : Comparable<K>> update(
        model: Model,
        node: de.flapdoodle.tab.model.Node.Calculated<K>,
        calculation: Calculation<K>
    ): Model {
        val sourceVariables = calculation.variables()
        val neededInputs = node.calculations.inputSlots(sourceVariables)
        val missingSources = neededInputs.filter { it.source == null }
        if (missingSources.isEmpty()) {
            val sources = neededInputs.associateBy { it.source!! }
            val input2data = sources.map { (source, input) ->
                input to dataOf(calculation.indexType(), source, model)
            }
            val variableDataMap = input2data.flatMap { (input, data) ->
                input.mapTo.map { v -> v to data }
            }.toMap()
            return calculate(model, node, calculation, variableDataMap)
        } else {
            logger.info { "missing sources: $missingSources" }
            return clear(model, node, calculation)
        }

//        return model
    }

    private fun <K: Comparable<K>> dataOf(
        indexType: TypeInfo<K>,
        source: Source,
        model: Model
    ): Data {
        val node = model.node(source.node)
        val data = when (source) {
            is Source.ColumnSource<*> -> {
                when (node) {
                    is de.flapdoodle.tab.model.Node.HasColumns<*> -> {
                        require(node.indexType==indexType) {"wrong index type: $indexType != ${node.indexType}"}
                        node.column(source.columnId)
                    }
                    else -> {
                        throw IllegalArgumentException("mismatch")
                    }
                }
            }

            is Source.ValueSource -> {
                when (node) {
                    is de.flapdoodle.tab.model.Node.HasValues -> node.value(source.valueId)
                    else -> {
                        throw IllegalArgumentException("mismatch")
                    }
                }
            }
        }
        return data
    }

    private fun <K : Comparable<K>> calculate(
        model: Model,
        node: de.flapdoodle.tab.model.Node.Calculated<K>,
        calculation: Calculation<K>,
        variableDataMap: Map<Variable, Data>
    ): Model {
        return when (calculation) {
            is Calculation.Aggregation<K> -> calculateAggregate(model, node, calculation, variableDataMap)
            is Calculation.Tabular<K> -> calculateTabular(model, node, calculation, variableDataMap)
        }
    }

    private fun <K : Comparable<K>> clear(
        model: Model,
        node: de.flapdoodle.tab.model.Node.Calculated<K>,
        calculation: Calculation<K>
    ): Model {
        return when (calculation) {
            is Calculation.Aggregation<K> -> clearAggregate(model, node, calculation)
            is Calculation.Tabular<K> -> clearTabular(model, node, calculation)
        }
    }

    private fun <K : Comparable<K>> calculateAggregate(
        model: Model,
        node: de.flapdoodle.tab.model.Node.Calculated<K>,
        calculation: Calculation.Aggregation<K>,
        variableDataMap: Map<Variable, Data>
    ): Model {
        val valueMap: Map<Variable, Evaluated<*>> = variableDataMap.map { (v, data) ->
            v to when (data) {
                is SingleValue<out Any> -> singleValueAsEvaluated(data)
                is Column<*,*> -> columnAsEvaluated(data)
            }
        }.toMap()
        val result = try {
             calculation.evaluate(valueMap)
        } catch (ex: BaseException) {
            try {
                Evaluated.ofNull(calculation.evaluateType(valueMap))
            } catch (ex: BaseException) {
                logger.warning("null type evaluation failed", ex)
                null
            }
        }
        val changedNode: de.flapdoodle.tab.model.Node.Calculated<K> = setValue(node, calculation, result)
        return model.copy(nodes = model.nodes().map { if (it.id == changedNode.id) changedNode else it })
    }

    private fun <K : Comparable<K>> clearAggregate(
        model: Model,
        node: de.flapdoodle.tab.model.Node.Calculated<K>,
        calculation: Calculation.Aggregation<K>
    ): Model {
        val changedNode: de.flapdoodle.tab.model.Node.Calculated<K> = setValue<K, Unit>(node, calculation, null)
//        val changedNode: de.flapdoodle.tab.model.Node.Calculated<K> = clearValue(node, calculation)
        return model.copy(nodes = model.nodes().map { if (it.id == changedNode.id) changedNode else it })
    }

    private fun <T: Any> singleValueAsEvaluated(data: SingleValue<T>): Evaluated<T> {
        return Evaluated.ofNullable(data.valueType, data.value)
    }

    private fun <V: Any> columnAsEvaluated(data: Column<*, V>): Evaluated<out Any> {
        return Evaluated.ofNullable(
            IndexMap.IndexMapTypeInfo(data.valueType),
            IndexMap.asMap(data)
        )
    }

    private fun <K : Comparable<K>> calculateTabular(
        updated: Model,
        node: de.flapdoodle.tab.model.Node.Calculated<K>,
        calculation: Calculation.Tabular<K>,
        variableDataMap: Map<Variable, Data>
    ): Model {
        val (columns, values) = variableDataMap.entries.partition { it.value is Column<*, *> }
        val columnsMap = columns.map {  it.key to it.value as Column<K, Any> }
        val singleValueMap = values.map { it.key to singleValueAsEvaluated(it.value as SingleValue<Any>) }
        return calculateTabular(updated, node, calculation, columnsMap, singleValueMap)
    }

    private fun <K : Comparable<K>> calculateTabular(
        updated: Model,
        node: de.flapdoodle.tab.model.Node.Calculated<K>,
        calculation: Calculation.Tabular<K>,
        columnsMap: List<Pair<Variable, Column<K, Any>>>,
        singleValueMap: List<Pair<Variable, Evaluated<Any>>>
    ): Model {
        val interpolated = sortAndInterpolate(columnsMap)
        val result = interpolated.index.mapNotNull {
            try {
                val result = calculation.evaluate(interpolated.variablesAt(it) + singleValueMap.toMap())
                if (result != null) it to result else null
            } catch (ex: BaseException) {
                ex.printStackTrace()
                null
            }
        }.toMap()

        val changedNode: de.flapdoodle.tab.model.Node.Calculated<K> = setTable(node, calculation, result)
        return updated.copy(nodes = updated.nodes().map { if (it.id == changedNode.id) changedNode else it })
    }

    private fun <K : Comparable<K>> clearTabular(
        updated: Model,
        node: de.flapdoodle.tab.model.Node.Calculated<K>,
        calculation: Calculation.Tabular<K>
    ): Model {
        val changedNode: de.flapdoodle.tab.model.Node.Calculated<K> = setTable(node, calculation, emptyMap())
//        val changedNode: de.flapdoodle.tab.model.Node.Calculated<K> = clearTable(node, calculation)
        return updated.copy(nodes = updated.nodes().map { if (it.id == changedNode.id) changedNode else it })
    }

    private fun <K : Comparable<K>> sortAndInterpolate(columns: List<Pair<Variable, Column<K, Any>>>): InterpolatorColumns<K> {
        val index = columns.flatMap { it.second.index() }.toSet()
        val map = columns.map { (variable, column) ->
            variable to interpolatorFor(index, column) // Interpolator.valueAtOffset(column.valueType, column.values).interpolatedAt(index)
        }
        return InterpolatorColumns(index, map)
    }

    private fun <K: Comparable<K>, V: Any> interpolatorFor(index: Set<K>, column: Column<K, V>): Interpolator<in K, V> {
        val factory = DefaultInterpolatorFactoryLookup.interpolatorFactoryFor(
            column.interpolationType,
            column.indexType,
            column.valueType
        )
        return factory.interpolatorFor(index, column.values)
    }

    data class InterpolatorColumns<K : Any>(
        val index: Set<K>,
        val varsMap: List<Pair<Variable, Interpolator<in K, Any>>>
    ) {
        fun variablesAt(index: K): Map<Variable, Evaluated<out Any>> {
            return varsMap.map { it.first to it.second.interpolated(index) }.toMap()
        }
    }

    private fun <K : Comparable<K>, V: Any> setValue(
        node: de.flapdoodle.tab.model.Node.Calculated<K>,
        calculation: Calculation.Aggregation<K>,
        result: Evaluated<V>?
    ): de.flapdoodle.tab.model.Node.Calculated<K> {
        val changedNode = if (node.values.find(calculation.destination()) == null) {
            val newSingleValue = if (result != null) {
                SingleValue.ofNullable(calculation.name(), result.type(), result.wrapped(), calculation.destination())
            } else {
                SingleValue.ofNull(calculation.name(), Unknown::class, calculation.destination())
            }
            node.copy(values = node.values.addValue(newSingleValue))
        } else {
            node.copy(values = node.values.change(calculation.destination()) { old ->
                if (result != null) {
                    SingleValue.ofNullable(old.name, result.type(), result.wrapped(), old.id)
                } else {
                    SingleValue.ofNull(old.name, Unknown::class, old.id)
                }
            })
        }
        return changedNode
    }

    private fun <K : Comparable<K>> clearValue(
        node: de.flapdoodle.tab.model.Node.Calculated<K>,
        calculation: Calculation.Aggregation<K>
    ): de.flapdoodle.tab.model.Node.Calculated<K> {
        return node.copy(values = node.values.remove(calculation.destination()))
    }

    private fun <K : Comparable<K>> setTable(
        node: de.flapdoodle.tab.model.Node.Calculated<K>,
        calculation: Calculation.Tabular<K>,
        result: Map<K, Evaluated<out Any>>
    ): de.flapdoodle.tab.model.Node.Calculated<K> {
        // TODO multiple value types in result
//        if (result.isNotEmpty()) {
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
                        newColumn.copy(id = old.id, color = old.color)
                    })
                }
            }
            return changedNode
//        } else {
//            val existingColumn = node.columns.find(calculation.destination())
//            return if (existingColumn != null) {
//                node.copy(columns = node.columns.remove(calculation.destination()))
//            } else {
//                node
//            }
//        }
    }

    private fun <K : Comparable<K>> clearTable(
        node: de.flapdoodle.tab.model.Node.Calculated<K>,
        calculation: Calculation.Tabular<K>
    ): de.flapdoodle.tab.model.Node.Calculated<K> {
        return node.copy(columns = node.columns.remove(calculation.destination()))
    }

    private fun <K : Comparable<K>> column(
        result: Map<K, Evaluated<out Any>>,
        calculation: Calculation.Tabular<K>
    ): Column<K, out Any> {
        val valueType: TypeInfo<out Any> = if (result.isEmpty()) {
            TypeInfo.of(Unknown::class.java)
        } else {
            require(result.isNotEmpty()) { "result is empty" }
            val valueTypes = result.values.map { it.type() }.toSet()
            require(valueTypes.size == 1) { "more than one value type: $result" }
            valueTypes.toList().one { true }
        }

        val column = Column(
            name = calculation.name(),
            indexType = calculation.indexType(),
            valueType = valueType,
            values = emptyMap(),
            id = calculation.destination()
        )
        return column.set(result.mapValues { it.value.wrapped() })
    }


    private fun verticesAndEdges(
        model: Model
    ): Collection<VerticesAndEdges<Vertex, DefaultEdge>> {
        val graph = Graphs.with(Graphs.directedGraphBuilder<Vertex>())
            .build { builder ->
                model.nodes().forEach { node ->
                    when (node) {
                        is de.flapdoodle.tab.model.Node.Constants -> {
                            node.values.forEach { value ->
                                builder.addVertex(Vertex.SingleValue(node.id, value.id))
                            }
                        }

                        is de.flapdoodle.tab.model.Node.Table<*> -> {
                            node.columns.forEach { column ->
                                builder.addVertex(Vertex.Column(node.id, column.id))
                            }
                        }

                        is de.flapdoodle.tab.model.Node.Calculated<out Comparable<*>> -> {
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

        logger.debug {
            val dot = GraphAsDot.builder<Vertex> { it ->
                when (it) {
                    is Vertex.Column -> "column(${it.node}:${it.columnId})"
                    is Vertex.SingleValue -> "value(${it.node}:${it.valueId})"
                }
            }
                .build().asDot(graph)
            "----------------\n$dot\n----------------\n"
        }

        return Graphs.rootsOf(graph)
    }
}