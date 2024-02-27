package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.DataId

data class Calculations<K: Comparable<K>>(
    val list: List<Calculation<K>> = emptyList(),
    val inputs: List<InputSlot> = inputSlots(list)
) {
    val input2destinations: Map<InputSlot, List<DataId>> by lazy {
        val destinationMap: Map<Variable, DataId> = list.flatMap {
            it.formula.variables().map { v ->
                when (it) {
                    is Calculation.Tabular<*> -> v to it.destination
                    is Calculation.Aggregation -> v to it.destination
                }
            }
        }.toMap()
        inputs.associateWith { it.mapTo.mapNotNull { v -> destinationMap[v] } }
    }

    fun changeFormula(id: Id<Calculation<*>>, newFormula: String): Calculations<K> {
        val changedList = list.map { if (it.id==id) it.changeFormula(newFormula) else it  }
        return copy(list = changedList, inputs = merge(inputs, inputSlots(changedList)))
    }

    fun connect(input: Id<InputSlot>, source: Source): Calculations<K> {
        return copy(inputs = inputs.map { if (it.id == input) it.copy(source = source) else it })
    }

    fun forEach(action: (Calculation<K>) -> Unit) {
        list.forEach(action)
    }

    fun destinations(inputSlot: InputSlot) = input2destinations[inputSlot]

    companion object {
        fun merge(old: List<InputSlot>, new: List<InputSlot>): List<InputSlot> {
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

        fun inputSlots(list: List<Calculation<*>>): List<InputSlot> {
            val nameMap = groupByName(list)
            return nameMap.map { InputSlot(name = it.key, mapTo = it.value.toSet()) }
                .sortedBy { it.name }
        }

        private fun groupByName(list: List<Calculation<*>>) =
            list.flatMap { it.formula.variables() }.groupBy { it.name }
    }
}


