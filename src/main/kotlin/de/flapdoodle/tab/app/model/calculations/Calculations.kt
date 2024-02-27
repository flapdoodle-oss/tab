package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.DataId
import de.flapdoodle.tab.extensions.change
import de.flapdoodle.tab.types.change

data class Calculations<K: Comparable<K>>(
//    val list: List<Calculation<K>> = emptyList(),
    val aggregations: List<Calculation.Aggregation<K>> = emptyList(),
    val tabular: List<Calculation.Tabular<K>> = emptyList(),
    val inputs: List<InputSlot<K>> = inputSlots(aggregations + tabular)
) {

    fun changeFormula(id: Id<Calculation<*>>, newFormula: String): Calculations<K> {
        val agg = aggregations.change({ it.id == id}) { it.changeFormula(newFormula) }
        val tab = tabular.change({ it.id == id}) { it.changeFormula(newFormula) }
        return copy(aggregations = agg, tabular = tab, inputs = merge(inputs, inputSlots(agg + tab)))
    }

    fun connect(input: Id<InputSlot<*>>, source: Source): Calculations<K> {
        return copy(inputs = inputs.map { if (it.id == input) it.copy(source = source) else it })
    }

    fun forEach(action: (Calculation<K>) -> Unit) {
        aggregations.forEach(action)
        tabular.forEach(action)
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
            list.flatMap { it.formula.variables() }.groupBy { it.name }
    }
}


