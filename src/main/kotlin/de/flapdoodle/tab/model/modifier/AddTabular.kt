package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType

data class AddTabular(
    val id: Id<out Node.Calculated<out Comparable<*>>>,
    val name: Name,
    val expression: String,
    val interpolationType: InterpolationType
) : CalculationModifier(
    id = id,
    change = { calculated -> addAggregation(calculated, name, expression, interpolationType) }
) {
    companion object {
        private fun <K : Comparable<K>> addAggregation(
            node: Node.Calculated<K>,
            name: Name,
            expression: String,
            interpolationType: InterpolationType
        ): Node.Calculated<K> {
            return node.addTabular(
                Calculation.Tabular(
                    indexType = node.indexType,
                    name = name,
                    formula = EvalFormulaAdapter(expression),
                    interpolationType = interpolationType
                )
            )
        }
    }
}