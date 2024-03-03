package de.flapdoodle.tab.app.model.change

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.data.SingleValue
import de.flapdoodle.tab.app.model.data.SingleValueId

sealed class ModelChange {
    sealed class ConstantsChange(open val id: Id<out Node.Constants>) : ModelChange()
    data class AddValue(override val id: Id<out Node.Constants>, val value: SingleValue<out Any>): ConstantsChange(id)
    data class ChangeValue(override val id: Id<out Node.Constants>, val valueId: SingleValueId, val value: Any?): ConstantsChange(id)
    data class RemoveValue(override val id: Id<out Node.Constants>, val valueId: SingleValueId): ConstantsChange(id)

    sealed class CalculationChange(open val id: Id<out Node.Calculated<out Comparable<*>>>): ModelChange()

    class AddAggregation(id: Id<Node.Calculated<*>>, val name: String, val expression: String) : CalculationChange(id)
    data class ChangeFormula(
        override val id: Id<out Node.Calculated<out Comparable<*>>>,
        val calculationId: Id<Calculation<*>>,
        val formula: String
    ): CalculationChange(id)
    class RemoveFormula(id: Id<Node.Calculated<*>>, val calculationId: Id<Calculation<*>>) : CalculationChange(id)
}