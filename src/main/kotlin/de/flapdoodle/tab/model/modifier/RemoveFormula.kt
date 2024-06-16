package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.Calculation

data class RemoveFormula(
    val id: Id<out Node.Calculated<out Comparable<*>>>,
    val calculationId: Id<Calculation<*>>
) : CalculationModifier(
    id = id,
    change = { table -> removeColumn(table, calculationId) }
) {
    companion object {
        private fun <K: Comparable<K>> removeColumn(table: Node.Calculated<K>, calculationId: Id<Calculation<*>>): Node.Calculated<K> {
            return table.copy(calculations = table.calculations.removeFormula(calculationId))
        }
    }
}