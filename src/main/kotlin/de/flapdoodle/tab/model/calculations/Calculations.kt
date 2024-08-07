package de.flapdoodle.tab.model.calculations

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.SingleValueId
import de.flapdoodle.tab.model.errors.Error
import de.flapdoodle.tab.types.change
import de.flapdoodle.tab.types.one
import javafx.scene.paint.Color

data class Calculations<K: Comparable<K>>(
    private val indexType: TypeInfo<K>,
    private val aggregations: List<Calculation.Aggregation<K>> = emptyList(),
    private val tabular: List<Calculation.Tabular<K>> = emptyList(),
    private val inputs: List<InputSlot<K>> = mergedInputSlots(aggregations + tabular)
) {
    init {
        val aggregationsById = aggregations.groupBy { it.id }.filter { it.value.size > 1 }
        require(aggregationsById.isEmpty()) { "aggregation id collisions: ${aggregationsById.keys}"}
        val tabularById = tabular.groupBy { it.id }.filter { it.value.size > 1 }
        require(tabularById.isEmpty()) { "aggregation id collisions: ${tabularById.keys}"}
    }

    fun aggregations() = aggregations
    fun tabular() = tabular
    fun inputs() = inputs

    fun inputSlot(id: Id<InputSlot<*>>): InputSlot<K> {
        return inputs.one { it.id == id }
    }


    fun addAggregation(aggregation: Calculation.Aggregation<K>): Calculations<K> {
        return copy(
            aggregations = aggregations + aggregation,
            inputs = merge(inputs, mergedInputSlots(aggregations + aggregation + tabular))
        )
    }

    fun addTabular(tab: Calculation.Tabular<K>): Calculations<K> {
        return copy(
            tabular = tabular + tab,
            inputs = merge(inputs, mergedInputSlots(aggregations + tabular + tab))
        )
    }

    fun removeFormula(calculationId: Id<Calculation<*>>): Calculations<K> {
        val changedAggregations = aggregations.filter { it.id != calculationId }
        val changedTabular = tabular.filter { it.id != calculationId }
        return copy(aggregations = changedAggregations, tabular = changedTabular, inputs = merge(inputs, mergedInputSlots(changedAggregations + changedTabular)))
    }

    fun aggregation(id: SingleValueId): Calculation.Aggregation<K> {
        return aggregations.one { it.destination() == id }
    }

    fun tabular(id: ColumnId): Calculation.Tabular<K> {
        return tabular.one { it.destination() == id }
    }

    fun inputSlots(variable: Set<Variable>): List<InputSlot<K>> {
        return inputs.filter { it.mapTo.intersect(variable).isNotEmpty() }
    }

    @Deprecated("dont use")
    fun changeFormula(id: Id<Calculation<*>>, name: Name, newExpression: Expression): Calculations<K> {
        val changedAggregations = aggregations.change(Calculation.Aggregation<K>::id, id) { it.changeFormula(name, newExpression) }
        val changedTabular = tabular.change(Calculation.Tabular<K>::id, id) { it.changeFormula(name, newExpression) }
        return copy(aggregations = changedAggregations, tabular = changedTabular, inputs = merge(inputs, mergedInputSlots(changedAggregations + changedTabular)))
    }

    fun changeAggregation(id: Id<Calculation<*>>, name: Name, newExpression: Expression, color: Color): Calculations<K> {
        val changedAggregations = aggregations.change(Calculation.Aggregation<K>::id, id) { it.changeFormula(name, newExpression, color) }
        return copy(aggregations = changedAggregations, inputs = merge(inputs, mergedInputSlots(changedAggregations + tabular)))
    }

    fun changeAggregationError(id: Id<Calculation<*>>, error: Error?): Calculations<K> {
        val changedAggregations = aggregations.change(Calculation.Aggregation<K>::id, id) { it.copy(error = error) }
        return copy(aggregations = changedAggregations)
    }

    fun changeTabular(id: Id<Calculation<*>>, name: Name, newExpression: Expression, color: Color, interpolationType: InterpolationType): Calculations<K> {
        val changedTabular = tabular.change(Calculation.Tabular<K>::id, id) { it.changeFormula(name, newExpression, color, interpolationType) }
        return copy(tabular = changedTabular, inputs = merge(inputs, mergedInputSlots(aggregations + changedTabular)))
    }

    fun changeTabularError(id: Id<Calculation<*>>, error: Error?): Calculations<K> {
        val changedTabular = tabular.change(Calculation.Tabular<K>::id, id) { it.copy(error = error) }
        return copy(tabular = changedTabular)
    }

    fun connect(input: Id<InputSlot<*>>, source: Source): Calculations<K> {
        return copy(inputs = inputs.map { if (it.id == input) it.copy(source = source) else it })
    }

    fun forEach(action: (Calculation<K>) -> Unit) {
        aggregations.forEach(action)
        tabular.forEach(action)
    }

    fun removeConnectionsFrom(id: Id<out Node>): Calculations<K> {
        return copy(inputs = inputs.map {
            if (it.source != null && it.source.node == id) {
                it.copy(source = null)
            } else it
        })
    }

    fun removeConnectionFrom(input: Id<InputSlot<*>>, id: Id<out Node>, source: Id<out Source>): Calculations<K> {
        return copy(inputs = inputs.map {
            if (it.id == input && it.source != null && it.source.node == id && it.source.id == source) {
                it.copy(source = null)
            } else it
        })
    }

    companion object {
        fun <K: Comparable<K>> merge(old: List<InputSlot<K>>, new: List<InputSlot<K>>): List<InputSlot<K>> {
            val oldByName = old.associateBy { it.name }

            val oldByVarId = old.flatMap { it.mapTo.map { v -> v.id to it } }.toMap()

            val copyFromOldIfVarsAreAsSubset = new.map { input ->
                val oldInput = oldByName[input.name]
                if (oldInput!=null && oldInput.mapTo.containsAll(input.mapTo)) {
                    oldInput.copy(mapTo = input.mapTo)
                } else {
                    val singleOldInputForAllVars = input.mapTo.mapNotNull { oldByVarId[it.id] }.toSet().firstOrNull()
                    if (singleOldInputForAllVars!=null) {
                        input.copy(source = singleOldInputForAllVars.source, color = singleOldInputForAllVars.color)
                    }
                    else input
                }
            }
            return copyFromOldIfVarsAreAsSubset.sortedBy { it.name }
        }

        fun <K: Comparable<K>> mergedInputSlots(list: List<Calculation<K>>): List<InputSlot<K>> {
            val nameMap = groupByName(list)
            return nameMap.map { InputSlot<K>(name = it.key, mapTo = it.value.toSet()) }
                .sortedBy { it.name }
        }

        private fun groupByName(list: List<Calculation<*>>) =
            list.flatMap { it.variables() }.filter { it.name != Variables.INDEX_NAME }.groupBy { it.name }
    }
}


