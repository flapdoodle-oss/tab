package de.flapdoodle.tab.model.change

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node.*
import de.flapdoodle.tab.model.Title
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.model.data.SingleValueId

sealed class ModelChange {
    sealed class ConstantsChange(open val id: Id<out Constants>) : ModelChange()
    data class ChangeConstantsProperties(override val id: Id<out Constants>, val name: Title): ConstantsChange(id)
    data class AddValue(override val id: Id<out Constants>, val value: SingleValue<out Any>): ConstantsChange(id)
    data class ChangeValue(override val id: Id<out Constants>, val valueId: SingleValueId, val value: Any?): ConstantsChange(id)
    data class RemoveValue(override val id: Id<out Constants>, val valueId: SingleValueId): ConstantsChange(id)
    data class ChangeValueProperties(override val id: Id<out Constants>, val valueId: SingleValueId, val name: Name): ConstantsChange(id)

    sealed class TableChange(open val id: Id<out Table<out Comparable<*>>>): ModelChange()
    data class ChangeTableProperties(override val id: Id<out Table<out Comparable<*>>>, val name: Title): TableChange(id)
    data class AddColumn<K: Comparable<K>>(override val id: Id<out Table<out Comparable<*>>>, val column: Column<K, out Any>): TableChange(id)
    data class ChangeColumnProperties<K: Comparable<K>>(
        override val id: Id<out Table<out Comparable<*>>>,
        val columnId: ColumnId,
        val name: Name,
        val interpolationType: InterpolationType,
    ): TableChange(id)
    data class SetColumns<K: Comparable<K>>(
        override val id: Id<out Table<out Comparable<*>>>,
        val index: K,
        val changes: List<Pair<ColumnId, Any?>>
    ): TableChange(id)
    data class MoveValues<K: Comparable<K>>(
        override val id: Id<out Table<out Comparable<*>>>,
        val lastIndex: K,
        val index: K
    ): TableChange(id) {
        init {
            require(lastIndex != index) { "same index: $lastIndex" }
        }
    }
    data class RemoveValues<K: Comparable<K>>(
        override val id: Id<out Table<out Comparable<*>>>,
        val index: K
    ): TableChange(id)
    data class RemoveColumn(override val id: Id<out Table<out Comparable<*>>>, val columnId: ColumnId): TableChange(id)

    sealed class CalculationChange(open val id: Id<out Calculated<out Comparable<*>>>): ModelChange()
    data class ChangeCalculationProperties(override val id: Id<out Calculated<out Comparable<*>>>, val name: Title): CalculationChange(id)
    data class AddAggregation(override val id: Id<Calculated<*>>, val name: Name, val expression: String) : CalculationChange(id)
    data class AddTabular(override val id: Id<Calculated<*>>, val name: Name, val expression: String) : CalculationChange(id)
    data class ChangeFormula(
        override val id: Id<out Calculated<out Comparable<*>>>,
        val calculationId: Id<Calculation<*>>,
        val formula: String
    ): CalculationChange(id)
    data class RemoveFormula(override val id: Id<Calculated<*>>, val calculationId: Id<Calculation<*>>) : CalculationChange(id)
}