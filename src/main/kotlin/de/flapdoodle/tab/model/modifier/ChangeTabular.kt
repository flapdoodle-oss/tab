package de.flapdoodle.tab.model.modifier

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType

data class ChangeTabular(
    val id: Id<out Node.Calculated<out Comparable<*>>>,
    val calculationId: Id<Calculation<*>>,
    val name: Name,
    val formula: Expression,
    val interpolationType: InterpolationType
) : CalculationModifier(
    id = id,
    change = { calculated -> change(calculated, calculationId, name, formula, interpolationType) }
) {
    companion object {
        fun  <K : Comparable<K>> change(
            calculated: Node.Calculated<K>,
            calculationId: Id<Calculation<*>>,
            name: Name,
            formula: Expression,
            interpolationType: InterpolationType
        ): Node.Calculated<K> {
            return calculated.copy(calculations = calculated.calculations.changeTabular(calculationId, name, formula, interpolationType))
        }
    }
}

