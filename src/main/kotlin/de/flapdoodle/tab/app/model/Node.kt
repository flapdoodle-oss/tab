package de.flapdoodle.tab.app.model

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.calculations.Calculations
import de.flapdoodle.tab.app.model.calculations.InputSlot
import de.flapdoodle.tab.app.model.calculations.adapter.EvalFormulaAdapter
import de.flapdoodle.tab.app.model.change.ModelChange
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.*
import de.flapdoodle.tab.types.one
import kotlin.reflect.KClass

sealed class Node {
    abstract fun removeConnectionsFrom(id: Id<out Node>): Node

    abstract val name: String
    abstract val id: Id<out Node>
    abstract val position: Position

    abstract fun apply(change: ModelChange): Node

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
        val columns: Columns<K>

        fun column(id: ColumnId<*>): Column<K, out Any> {
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
        override val name: String,
        override val values: SingleValues = SingleValues(),
        override val id: Id<Constants> = Id.nextId(Constants::class),
        override val position: Position = Position(0.0, 0.0)
    ) : Node(), HasValues {

        fun addValue(value: SingleValue<*>): Constants {
            return copy(values = values.addValue(value))
        }

        override fun removeConnectionsFrom(id: Id<out Node>) = this

        override fun apply(change: ModelChange): Constants {
            if (change is ModelChange.ConstantsChange && change.id==id) {
                when (change) {
                    is ModelChange.AddValue -> {
                        return addValue(change.value)
                    }
                    is ModelChange.ChangeValue -> {
                        return copy(values = values.set(change.valueId, change.value))
                    }
                    is ModelChange.RemoveValue -> {
                        return copy(values = values.remove(change.valueId))
                    }
                }
            }
            return this
        }
    }

    data class Table<K: Comparable<K>> (
        override val name: String,
        val indexType: KClass<K>,
        override val columns: Columns<K> = Columns(),
        override val id: Id<Table<*>> = Id.nextId(Table::class),
        override val position: Position = Position(0.0, 0.0)
    ) : Node(), HasColumns<K> {

        override fun removeConnectionsFrom(id: Id<out Node>) = this

        override fun apply(change: ModelChange): Table<K> {
            if (change is ModelChange.TableChange) {
                when (change) {
                    is ModelChange.AddColumn<out Comparable<*>> -> {
                        if (change.id == id) {
                            require(indexType==change.column.indexType) {"type mismatch: ${change.column}"}
                            return copy(columns = columns.addColumn(change.column as Column<K, out Any>))
                        }
                    }
                    is ModelChange.MoveValues<out Comparable<*>> -> {
                        if (change.id == id) {
                            return copy(columns = columns.moveValues(change.lastIndex as K, change.index as K))
                        }
                    }
//                    is ModelChange.SetColumn<out Comparable<*>> -> {
//                        if (change.id == id) {
//                            return copy(columns = columns.set(change.columnId as ColumnId<K> , change.index as K, change.value))
//                        }
//                    }
                    is ModelChange.SetColumns<out Comparable<*>> -> {
                        if (change.id == id) {
                            return copy(columns = change.changes.fold(columns) { c: Columns<K>, idAndValue: Pair<ColumnId<out Comparable<*>>, Any?> ->
                                c.set(idAndValue.first as ColumnId<K>, change.index as K, idAndValue.second)
                            })
                        }
                    }
                    is ModelChange.RemoveColumn -> {
                        if (change.id == id) {
                            return copy(columns = columns.remove(change.columnId as ColumnId<K>))
                        }
                    }
                }
            }
            return this
        }
    }

    data class Calculated<K: Comparable<K>>(
        override val name: String,
        val indexType: KClass<K>,
        val calculations: Calculations<K> = Calculations(),
        override val columns: Columns<K> = Columns(),
        override val values: SingleValues = SingleValues(),
        override val id: Id<Calculated<*>> = Id.nextId(Calculated::class),
        override val position: Position = Position(0.0, 0.0)
    ): Node(), HasColumns<K>, HasValues {

        fun addAggregation(aggregation: Calculation.Aggregation<K>): Calculated<K> {
            return copy(calculations = calculations.addAggregation(aggregation))
        }

        fun addTabular(tabular: Calculation.Tabular<K>): Calculated<K> {
            return copy(calculations = calculations.addTabular(tabular))
        }

        fun connect(input: Id<InputSlot<*>>, source: Source): Calculated<K> {
            return copy(calculations = calculations.connect(input, source))
        }

        override fun removeConnectionsFrom(id: Id<out Node>): Calculated<K> {
            return copy(calculations = calculations.removeConnectionsFrom(id))
        }

        override fun apply(change: ModelChange): Calculated<K> {
            if (change is ModelChange.CalculationChange) {
                when (change) {
                    is ModelChange.ChangeFormula -> {
                        if (change.id == id) {
                            return copy(calculations = calculations.changeFormula(change.calculationId,change.formula))
                        }
                    }
                    is ModelChange.RemoveFormula -> {
                        if (change.id == id) {
//                            return copy(calculations = calculations.removeFormula(change.calculationId))
                        }
                    }
                    is ModelChange.AddAggregation -> {
                        if (change.id == id) {
                            return copy(calculations = calculations.addAggregation(
                                Calculation.Aggregation(
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
                                    name = change.name,
                                    formula = EvalFormulaAdapter(change.expression),
                                    destination = ColumnId(indexType)
                                )
                            ))
                        }
                    }
                }
            }
            return this
        }
    }
}