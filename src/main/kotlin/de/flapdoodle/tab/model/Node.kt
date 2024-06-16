package de.flapdoodle.tab.model

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Calculations
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.*
import de.flapdoodle.tab.types.one
import de.flapdoodle.tab.types.oneOrNull

sealed class Node {
    abstract fun removeConnectionsFrom(id: Id<out Node>): Node
    abstract fun removeConnectionFrom(input: Id<InputSlot<*>>, id: Id<out Node>, source: Id<out Source>): Node
    abstract fun sources(): Set<Source>

    abstract val name: Title
    abstract val id: Id<out Node>
    abstract val position: Position
    abstract val size: Size?

    abstract fun moveTo(position: Position): Node
    abstract fun resizeTo(position: Position, size: Size): Node

//    fun data(id: DataId): Data {
//        return when (id) {
//            is ColumnId<*> -> {
//                require(this is HasColumns<*>) { "mismatch"}
//                this.column(id)
//            }
//            is SingleValueId -> {
//                require(this is HasValues) {"mismatch"}
//                this.value(id)
//            }
//        }
//    }

    interface HasColumns<K: Comparable<K>> {
        val indexType: TypeInfo<K>
        val columns: Columns<K>

        fun column(id: ColumnId): Column<K, out Any> {
            return columns.columns().one { it.id == id }
        }

        fun findColumn(id: ColumnId): Column<K, out Any>? {
            return columns.columns().oneOrNull { it.id == id }
        }
    }

    interface HasValues {
        val values: SingleValues

        fun value(id: SingleValueId): SingleValue<out Any> {
            return values.values.one { it.id == id }
        }

        fun findValue(id: SingleValueId): SingleValue<out Any>? {
            return values.values.oneOrNull { it.id == id }
        }
    }

    data class Constants(
        override val name: Title,
        override val values: SingleValues = SingleValues(),
        override val id: Id<Node.Constants> = Id.nextId(Node.Constants::class),
        override val position: Position = Position(0.0, 0.0),
        override val size: Size? = null
    ) : Node(), HasValues {

        fun addValue(value: SingleValue<*>): Node.Constants {
            return copy(values = values.addValue(value))
        }

        override fun removeConnectionsFrom(id: Id<out Node>) = this
        override fun removeConnectionFrom(input: Id<InputSlot<*>>, id: Id<out Node>, source: Id<out Source>) = this
        override fun sources(): Set<Source> = emptySet()

        override fun moveTo(position: Position): Node {
            return copy(position = position)
        }

        override fun resizeTo(position: Position, size: Size): Node {
            return copy(position = position, size=size)
        }
    }

    data class Table<K: Comparable<K>> (
        override val name: Title,
        override val indexType: TypeInfo<K>,
        override val columns: Columns<K> = Columns(),
        override val id: Id<Table<*>> = Id.nextId(Table::class),
        override val position: Position = Position(0.0, 0.0),
        override val size: Size? = null
    ) : Node(), HasColumns<K> {

        override fun removeConnectionsFrom(id: Id<out Node>) = this
        override fun removeConnectionFrom(input: Id<InputSlot<*>>, id: Id<out Node>, source: Id<out Source>) = this
        override fun sources(): Set<Source> = emptySet()

        override fun moveTo(position: Position): Node {
            return copy(position = position)
        }

        override fun resizeTo(position: Position, size: Size): Node {
            return copy(position = position, size = size)
        }
    }

    data class Calculated<K: Comparable<K>>(
        override val name: Title,
        override val indexType: TypeInfo<K>,
        val calculations: Calculations<K> = Calculations(indexType),
        override val columns: Columns<K> = Columns(),
        override val values: SingleValues = SingleValues(),
        override val id: Id<Calculated<*>> = Id.nextId(Calculated::class),
        override val position: Position = Position(0.0, 0.0),
        override val size: Size? = null
    ): Node(), HasColumns<K>,
        HasValues {

        fun addAggregation(aggregation: Calculation.Aggregation<K>): Node.Calculated<K> {
            return copy(calculations = calculations.addAggregation(aggregation))
        }

        fun addTabular(tabular: Calculation.Tabular<K>): Node.Calculated<K> {
            return copy(calculations = calculations.addTabular(tabular))
        }

        fun connect(input: Id<InputSlot<*>>, source: Source): Node.Calculated<K> {
            return copy(calculations = calculations.connect(input, source))
        }

        override fun removeConnectionsFrom(id: Id<out Node>): Node.Calculated<K> {
            return copy(calculations = calculations.removeConnectionsFrom(id))
        }

        override fun removeConnectionFrom(input: Id<InputSlot<*>>, id: Id<out Node>, source: Id<out Source>): Node.Calculated<K> {
            return copy(calculations = calculations.removeConnectionFrom(input, id, source))
        }

        override fun sources(): Set<Source> {
            return calculations.inputs().mapNotNull { it.source }.toSet()
        }

        override fun moveTo(position: Position): Node {
            return copy(position = position)
        }

        override fun resizeTo(position: Position, size: Size): Node {
            return copy(position = position, size = size)
        }
    }
}