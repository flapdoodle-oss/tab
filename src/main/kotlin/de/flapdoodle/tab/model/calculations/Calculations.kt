package de.flapdoodle.tab.model.calculations

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.SingleValueId
import de.flapdoodle.tab.types.change
import de.flapdoodle.tab.types.one
import de.flapdoodle.tab.ui.views.dialogs.NewExpression
import kotlin.reflect.KClass

data class Calculations<K: Comparable<K>>(
    private val indexType: TypeInfo<K>,
    private val aggregations: List<Calculation.Aggregation<K>> = emptyList(),
    private val tabular: List<Calculation.Tabular<K>> = emptyList(),
    private val inputs: List<InputSlot<K>> = inputSlots(aggregations + tabular)
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

    fun addAggregation(aggregation: Calculation.Aggregation<K>): Calculations<K> {
        return copy(
            aggregations = aggregations + aggregation,
            inputs = merge(inputs, inputSlots(aggregations + aggregation + tabular))
        )
    }

    fun addTabular(tab: Calculation.Tabular<K>): Calculations<K> {
        return copy(
            tabular = tabular + tab,
            inputs = merge(inputs, inputSlots(aggregations + tabular + tab))
        )
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

    fun changeFormula(id: Id<Calculation<*>>, name: Name, newFormula: String): Calculations<K> {
        val changedAggregations = aggregations.change(Calculation.Aggregation<K>::id, id) { it.changeFormula(name, newFormula) }
        val changedTabular = tabular.change(Calculation.Tabular<K>::id, id) { it.changeFormula(name, newFormula) }
        return copy(aggregations = changedAggregations, tabular = changedTabular, inputs = merge(inputs, inputSlots(changedAggregations + changedTabular)))
    }

    fun changeFormula(id: Id<Calculation<*>>, name: Name, newExpression: Expression): Calculations<K> {
        val changedAggregations = aggregations.change(Calculation.Aggregation<K>::id, id) { it.changeFormula(name, newExpression) }
        val changedTabular = tabular.change(Calculation.Tabular<K>::id, id) { it.changeFormula(name, newExpression) }
        return copy(aggregations = changedAggregations, tabular = changedTabular, inputs = merge(inputs, inputSlots(changedAggregations + changedTabular)))
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
                        input.copy(source = singleOldInputForAllVars.source)
                    }
                    else input
                }
            }
            return copyFromOldIfVarsAreAsSubset.sortedBy { it.name }
        }

        fun <K: Comparable<K>> inputSlots(list: List<Calculation<K>>): List<InputSlot<K>> {
            val nameMap = groupByName(list)
            return nameMap.map { InputSlot<K>(name = it.key, mapTo = it.value.toSet()) }
                .sortedBy { it.name }
        }

        private fun groupByName(list: List<Calculation<*>>) =
            list.flatMap { it.variables() }.groupBy { it.name }
    }
}


