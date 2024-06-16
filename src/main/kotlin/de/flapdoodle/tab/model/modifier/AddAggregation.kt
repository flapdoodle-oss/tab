package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter

data class AddAggregation(
    val id: Id<out Node.Calculated<out Comparable<*>>>,
    val name: Name,
    val expression: String
) : CalculationModifier(
    id = id,
    change = { calculated -> addAggregation(calculated, name, expression) }
) {
    companion object {
        private fun <K : Comparable<K>> addAggregation(
            node: Node.Calculated<K>,
            name: Name,
            expression: String
        ): Node.Calculated<K> {
            return node.addAggregation(
                Calculation.Aggregation(
                    indexType = node.indexType,
                    name = name,
                    formula = EvalFormulaAdapter(expression)
                )
            )
        }
    }
}