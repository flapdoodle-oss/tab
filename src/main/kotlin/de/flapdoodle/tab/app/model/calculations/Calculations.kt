package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.kfx.types.Id

data class Calculations(
    val list: List<Calculation> = emptyList(),
    val inputs: List<InputSlot> = inputSlots(list)
) {
    fun changeFormula(id: Id<Calculation>, newFormula: String): Calculations {
        val changedList = list.map { if (it.id==id) it.changeFormula(newFormula) else it  }
        return copy(list = changedList, inputs = merge(inputs, inputSlots(changedList)))
    }

    companion object {
        fun merge(old: List<InputSlot>, new: List<InputSlot>): List<InputSlot> {
            val oldByName = old.associateBy { it.name }

            val oldByVarId = old.flatMap { it.mapTo.map { v -> v.id to it } }.toMap()

            val copyFromOldIfVarsAreAsSubset = new.map { input ->
                val old = oldByName[input.name]
                if (old!=null && old.mapTo.containsAll(input.mapTo)) {
                    old.copy(mapTo = input.mapTo)
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

        fun inputSlots(list: List<Calculation>): List<InputSlot> {
            val nameMap = groupByName(list)
            return nameMap.map { InputSlot(name = it.key, mapTo = it.value.toSet()) }
                .sortedBy { it.name }
        }

        private fun groupByName(list: List<Calculation>) =
            list.flatMap { it.formula.variables() }.groupBy { it.name }
    }
}


