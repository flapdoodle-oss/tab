package de.flapdoodle.tab.model.changes

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.*
import de.flapdoodle.tab.model.Node.Calculated
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.*
import de.flapdoodle.types.Either

sealed class Change {
    data class AddNode(val node: Node): Change()
    data class RemoveNode(val id: Id<out Node>): Change()

    data class Move(val id: Id<out Node>, val position: Position): Change()
    data class Resize(val id: Id<out Node>, val position: Position, val size: Size): Change()

    data class Connect(
        val startId: Id<out Node>,
        val startDataOrInput: Either<DataId, Id<InputSlot<*>>>,
        val endId: Id<out Node>,
        val endDataOrInput: Either<DataId, Id<InputSlot<*>>>
    ): Change()

    data class Disconnect(
        val endId: Id<out Node>,
        val input: Id<InputSlot<*>>,
        val source: Source
    ): Change()

    sealed class Constants(open val id: Id<out Node.Constants>) : Change() {
        data class Properties(override val id: Id<out Node.Constants>, val name: Title): Constants(id)
        data class AddValue(override val id: Id<out Node.Constants>, val value: SingleValue<out Any>): Constants(id)
        data class ChangeValue(override val id: Id<out Node.Constants>, val valueId: SingleValueId, val value: Any?): Constants(id)
        data class RemoveValue(override val id: Id<out Node.Constants>, val valueId: SingleValueId): Constants(id)
        data class ValueProperties(override val id: Id<out Node.Constants>, val valueId: SingleValueId, val name: Name): Constants(id)
    }

    sealed class Table(open val id: Id<out Node.Table<out Comparable<*>>>) : Change() {
        data class Properties(override val id: Id<out Node.Table<out Comparable<*>>>, val name: Title) :
            Table(id)

        data class AddColumn<K : Comparable<K>>(
            override val id: Id<out Node.Table<out Comparable<*>>>,
            val column: Column<K, out Any>
        ) : Table(id)

        data class ColumnProperties(
            override val id: Id<out Node.Table<out Comparable<*>>>,
            val columnId: ColumnId,
            val name: Name,
            val interpolationType: InterpolationType,
        ) : Table(id)

        data class SetColumns<K : Comparable<K>>(
            override val id: Id<out Node.Table<out Comparable<*>>>,
            val index: K,
            val changes: List<Pair<ColumnId, Any?>>
        ) : Table(id)

        data class MoveValues<K : Comparable<K>>(
            override val id: Id<out Node.Table<out Comparable<*>>>,
            val lastIndex: K,
            val index: K
        ) : Table(id) {
            init {
                require(lastIndex != index) { "same index: $lastIndex" }
            }
        }

        data class RemoveValues<K : Comparable<K>>(
            override val id: Id<out Node.Table<out Comparable<*>>>,
            val index: K
        ) : Table(id)

        data class RemoveColumn(override val id: Id<out Node.Table<out Comparable<*>>>, val columnId: ColumnId) :
            Table(id)
    }

    sealed class Calculation(open val id: Id<out Calculated<out Comparable<*>>>): Change() {
        data class Properties(override val id: Id<out Calculated<out Comparable<*>>>, val name: Title): Calculation(id)
        data class AddAggregation(override val id: Id<Calculated<*>>, val name: Name, val expression: String) : Calculation(id)
        data class AddTabular(override val id: Id<Calculated<*>>, val name: Name, val expression: String, val interpolationType: InterpolationType) : Calculation(id)
        data class ChangeAggregation(
            override val id: Id<out Calculated<out Comparable<*>>>,
            val calculationId: Id<de.flapdoodle.tab.model.calculations.Calculation<*>>,
            val name: Name,
            val formula: Expression
        ): Calculation(id)
        data class ChangeTabular(
            override val id: Id<out Calculated<out Comparable<*>>>,
            val calculationId: Id<de.flapdoodle.tab.model.calculations.Calculation<*>>,
            val name: Name,
            val formula: Expression,
            val interpolationType: InterpolationType
        ): Calculation(id)
        data class RemoveFormula(override val id: Id<Calculated<*>>, val calculationId: Id<de.flapdoodle.tab.model.calculations.Calculation<*>>) : Calculation(id)
    }
}