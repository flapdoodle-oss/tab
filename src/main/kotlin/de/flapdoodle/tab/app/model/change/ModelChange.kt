package de.flapdoodle.tab.app.model.change

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.data.Column
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValue
import de.flapdoodle.tab.app.model.data.SingleValueId

sealed class ModelChange {
    sealed class ConstantsChange(open val id: Id<out Node.Constants>) : ModelChange()
    data class AddValue(override val id: Id<out Node.Constants>, val value: SingleValue<out Any>): ConstantsChange(id)
    data class ChangeValue(override val id: Id<out Node.Constants>, val valueId: SingleValueId, val value: Any?): ConstantsChange(id)
    data class RemoveValue(override val id: Id<out Node.Constants>, val valueId: SingleValueId): ConstantsChange(id)

    sealed class TableChange(open val id: Id<out Node.Table<out Comparable<*>>>): ModelChange()
    data class AddColumn<K: Comparable<K>>(override val id: Id<out Node.Table<out Comparable<*>>>, val column: Column<K, out Any>): TableChange(id)
//    data class SetColumn<K: Comparable<K>>(
//        override val id: Id<out Node.Table<out Comparable<*>>>,
//        val columnId: ColumnId<K>,
//        val index: K,
//        val value: Any?
//    ): TableChange(id)
    data class SetColumns<K: Comparable<K>>(
        override val id: Id<out Node.Table<out Comparable<*>>>,
        val index: K,
        val changes: List<Pair<ColumnId<K>, Any?>>
    ): TableChange(id)
    data class MoveValues<K: Comparable<K>>(
        override val id: Id<out Node.Table<out Comparable<*>>>,
        val lastIndex: K,
        val index: K
    ): TableChange(id) {
        init {
            require(lastIndex != index) { "same index: $lastIndex" }
        }
    }
    data class RemoveColumn(override val id: Id<out Node.Table<out Comparable<*>>>, val columnId: ColumnId<out Comparable<*>>): TableChange(id)

    sealed class CalculationChange(open val id: Id<out Node.Calculated<out Comparable<*>>>): ModelChange()
    class AddAggregation(id: Id<Node.Calculated<*>>, val name: String, val expression: String) : CalculationChange(id)
    class AddTabular(id: Id<Node.Calculated<*>>, val name: String, val expression: String) : CalculationChange(id)
    data class ChangeFormula(
        override val id: Id<out Node.Calculated<out Comparable<*>>>,
        val calculationId: Id<Calculation<*>>,
        val formula: String
    ): CalculationChange(id)
    class RemoveFormula(id: Id<Node.Calculated<*>>, val calculationId: Id<Calculation<*>>) : CalculationChange(id)
}