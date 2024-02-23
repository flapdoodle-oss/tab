package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Input

// variablen über formeln hinweg zusammenführen??
// pures mappen auf den Namen?
// wenn in einer formel sich der name ändert, ändert man ihn dann auch in allen anderen Formeln?
data class Calculations(
    val list: List<Calculation> = emptyList(),
    private val inputVariableMap: Map<Input, Set<Variable>> = inputVariableMap(list)
) {
//    private val variableToCalculationMap = list.flatMap { it.formula.variables().map { v -> v to it } }.toMap()
//    private val inputToVariableNames = variableToCalculationMap.keys.groupBy { it.name }

    init {
    }
    fun inputs() = inputVariableMap.keys

    fun changeFormula(id: Id<Calculation>, newFormula: String): Calculations {
        val changedList = list.map { if (it.id==id) it.changeFormula(newFormula) else it  }
        return copy(list = changedList, inputVariableMap = inputVariableMap(list, changedList))
    }

    companion object {
        fun inputVariableMap(base: List<Calculation>, changed: List<Calculation>): Map<Input, Set<Variable>> {
            val a = groupByName(base)
            val b = groupByName(changed)


            println("A: $a")
            println("B: $b")
            return emptyMap()
        }

        fun inputVariableMap(list: List<Calculation>): Map<Input, Set<Variable>> {
            val nameMap = groupByName(list)
            return nameMap.map { Input(it.key) to it.value.toSet() }
                .sortedBy { it.first.name }
                .toMap(linkedMapOf())
        }

        private fun groupByName(list: List<Calculation>) =
            list.flatMap { it.formula.variables() }.groupBy { it.name }
    }
}

