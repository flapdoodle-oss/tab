package de.flapdoodle.tab.model

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.calculations.Calculation
import de.flapdoodle.tab.model.calculations.Calculations
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.*
import de.flapdoodle.tab.types.one

sealed class Node {
    abstract fun removeConnectionsFrom(id: Id<out Node>): Node
    abstract fun removeConnectionFrom(input: Id<InputSlot<*>>, id: Id<out Node>, source: Id<out Source>): Node

    abstract val name: Title
    abstract val id: Id<out Node>
    abstract val position: Position
    abstract val size: Size?

    abstract fun apply(change: ModelChange): Node
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
    }

    interface HasValues {
        val values: SingleValues

        fun value(id: SingleValueId): SingleValue<out Any> {
            return values.values.one { it.id == id }
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

        override fun apply(change: ModelChange): Node.Constants {
            if (change is ModelChange.ConstantsChange && change.id==id) {
                when (change) {
                    is ModelChange.ChangeConstantsProperties -> {
                        return copy(name = change.name)
                    }
                    is ModelChange.AddValue -> {
                        return addValue(change.value)
                    }
                    is ModelChange.ChangeValue -> {
                        return copy(values = values.set(change.valueId, change.value))
                    }
                    is ModelChange.ChangeValueProperties -> {
                        return copy(values = values.change(change.valueId) { it.copy(name = change.name) })
                    }
                    is ModelChange.RemoveValue -> {
                        return copy(values = values.remove(change.valueId))
                    }
                }
            }
            return this
        }

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

        override fun apply(change: ModelChange): Table<K> {
            if (change is ModelChange.TableChange) {
                when (change) {
                    is ModelChange.ChangeTableProperties -> {
                        return copy(name = change.name)
                    }
                    is ModelChange.AddColumn<out Comparable<*>> -> {
                        if (change.id == id) {
                            require(indexType==change.column.indexType) {"type mismatch: ${change.column}"}
                            return copy(columns = columns.addColumn(change.column as Column<K, out Any>))
                        }
                    }
                    is ModelChange.ChangeColumnProperties<out Comparable<*>> -> {
                        if (change.id == id) {
                            return copy(columns = columns.change(change.columnId) { it.copy(name = change.name, interpolationType = change.interpolationType) })
                        }
                    }
                    is ModelChange.MoveValues<out Comparable<*>> -> {
                        if (change.id == id) {
                            return copy(columns = columns.moveValues(change.lastIndex as K, change.index as K))
                        }
                    }
                    is ModelChange.RemoveValues<out Comparable<*>> -> {
                        if (change.id == id) {
                            return copy(columns = columns.removeValues(change.index as K))
                        }
                    }
//                    is ModelChange.SetColumn<out Comparable<*>> -> {
//                        if (change.id == id) {
//                            return copy(columns = columns.set(change.columnId as ColumnId<K> , change.index as K, change.value))
//                        }
//                    }
                    is ModelChange.SetColumns<out Comparable<*>> -> {
                        if (change.id == id) {
                            return copy(columns = change.changes.fold(columns) { c: Columns<K>, idAndValue: Pair<ColumnId, Any?> ->
                                c.set(idAndValue.first, change.index as K, idAndValue.second)
                            })
                        }
                    }
                    is ModelChange.RemoveColumn -> {
                        if (change.id == id) {
                            return copy(columns = columns.remove(change.columnId as ColumnId))
                        }
                    }
                }
            }
            return this
        }

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

        override fun apply(change: ModelChange): Node.Calculated<K> {
            if (change is ModelChange.CalculationChange) {
                when (change) {
                    is ModelChange.ChangeCalculationProperties -> {
                        return copy(name = change.name)
                    }
                    is ModelChange.ChangeFormula -> {
                        if (change.id == id) {
                            return copy(calculations = calculations.changeFormula(change.calculationId, change.name, change.formula))
                        }
                    }
                    is ModelChange.RemoveFormula -> {
                        if (change.id == id) {
                            // TODO noch offen?
                            throw IllegalArgumentException("not implemented")
//                            return copy(calculations = calculations.removeFormula(change.calculationId))
                        }
                    }
                    is ModelChange.AddAggregation -> {
                        if (change.id == id) {
                            return copy(calculations = calculations.addAggregation(
                                Calculation.Aggregation(
                                    indexType = indexType,
                                    name = change.name,
                                    formula = EvalFormulaAdapter(change.expression)
                                )
                            ))
                        }
                    }
                    is ModelChange.AddTabular -> {
                        if (change.id == id) {
                            return copy(calculations = calculations.addTabular(
                                Calculation.Tabular(
                                    indexType = indexType,
                                    name = change.name,
                                    formula = EvalFormulaAdapter(change.expression),
                                    destination = ColumnId()
                                )
                            ))
                        }
                    }
                }
            }
            return this
        }

        override fun moveTo(position: Position): Node {
            return copy(position = position)
        }

        override fun resizeTo(position: Position, size: Size): Node {
            return copy(position = position, size = size)
        }
    }
}