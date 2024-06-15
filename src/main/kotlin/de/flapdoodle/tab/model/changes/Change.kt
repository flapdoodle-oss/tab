package de.flapdoodle.tab.model.changes

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.*
import de.flapdoodle.tab.model.Node.Table
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.DataId
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

    sealed class Table(open val id: Id<out Node.Table<out Comparable<*>>>) : Change() {
        data class Properties(override val id: Id<out Node.Table<out Comparable<*>>>, val name: Title) :
            Table(id)

        data class AddColumn<K : Comparable<K>>(
            override val id: Id<out Node.Table<out Comparable<*>>>,
            val column: Column<K, out Any>
        ) : Table(id)

        data class ColumnProperties<K : Comparable<K>>(
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
}