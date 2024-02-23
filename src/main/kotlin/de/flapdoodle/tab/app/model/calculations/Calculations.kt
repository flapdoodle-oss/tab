package de.flapdoodle.tab.app.model.calculations

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

    companion object {
        fun inputVariableMap(list: List<Calculation>): Map<Input, Set<Variable>> {
            val nameMap = list.flatMap { it.formula.variables() }.groupBy { it.name }
            return nameMap.map { Input(it.key) to it.value.toSet() }
                .sortedBy { it.first.name }
                .toMap(linkedMapOf())
        }
    }
}

