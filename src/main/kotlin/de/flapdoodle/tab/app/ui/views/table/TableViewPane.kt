package de.flapdoodle.tab.app.ui.views.table

import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.controls.bettertable.Table
import de.flapdoodle.kfx.controls.bettertable.TableChangeListener
import de.flapdoodle.kfx.controls.bettertable.events.ReadOnlyState
import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.change.ModelChange
import de.flapdoodle.tab.app.model.data.Column
import de.flapdoodle.tab.app.model.data.Columns
import de.flapdoodle.tab.app.model.diff.Diff
import de.flapdoodle.tab.app.ui.ModelChangeListener
import de.flapdoodle.tab.extensions.change
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.layout.StackPane
import kotlin.reflect.KClass

class TableViewPane<K : Comparable<K>>(
    node: Node.Calculated<K>
) : StackPane() {
    private val nodeId = node.id

    private var columns = node.columns

    private val tableRows = SimpleObjectProperty(rowsOf(columns))
    private val indexColumn = indexColumn(node.indexType)
    private val tableColumns = SimpleObjectProperty(tableColumnsOff(indexColumn, columns))

    private val tableChangeListener: TableChangeListener<Row<K>> = object : TableChangeListener<Row<K>> {
        override fun changeCell(row: Row<K>, change: TableChangeListener.CellChange<Row<K>, out Any>): Row<K> {
            TODO("Not yet implemented")
        }

        override fun emptyRow(index: Int): Row<K> {
            return Row(null, emptyList())
        }

        override fun updateRow(row: Row<K>, changed: Row<K>) {
            TODO("Not yet implemented")
        }

        override fun removeRow(row: Row<K>) {
            TODO("Not yet implemented")
        }

        override fun insertRow(index: Int, row: Row<K>): Boolean {
            TODO("Not yet implemented")
        }
    }

    private val table2 = Table(tableRows, tableColumns, tableChangeListener, stateFactory = { ReadOnlyState(it) })

    private fun rowsOf(columns: Columns<K>): List<Row<K>> {
        return columns.index().map { index ->
            Row(index, columns.columns().map { c ->
                columnValue(c, index)
            })
        }
    }

    private fun tableColumnsOff(indexColumn: de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, out Any>,  columns: Columns<K>): List<de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, out Any>> {
        return listOf(indexColumn) + columns.columns().map {
            column(it)
        }
    }

    private fun indexColumn(indexType: KClass<K>): de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, K> {
        return IndexColumn(indexType)
    }

    private fun <V: Any> column(column: Column<K, V>): NormalColumn<K, V> {
        return NormalColumn(column)
    }

    interface TableColumn<K: Comparable<K>, V: Any> {
        fun applyChange(
            row: Row<K>,
            change: TableChangeListener.CellChange<Row<K>, out Any>
        ): Row<K>
    }

    data class IndexColumn<K: Comparable<K>>(val indexType: KClass<K>):
        de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, K>(
            label = "#",
            property = { row -> row.index },
            editable = false,
            converter = Converters.converterFor(indexType)
    ), TableColumn<K, K> {
        override fun applyChange(row: Row<K>, change: TableChangeListener.CellChange<Row<K>, out Any>): Row<K> {
            return row.copy(index = change.value as K?)
        }
    }

    data class NormalColumn<K: Comparable<K>, V: Any>(val column: Column<K, V>) :
        de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, V>(
            label = column.name,
            property = { row -> row.get(column) },
            editable = false,
            converter = Converters.converterFor(column.valueType)
        ), TableColumn<K, V> {
        override fun applyChange(row: Row<K>, change: TableChangeListener.CellChange<Row<K>, out Any>): Row<K> {
            return row.set(ColumnValue(column, change.value as V?))
        }

    }


    init {
        children.add(table2)
    }

    fun update(node: Node.Calculated<K>) {
//        val columnChanges = Diff.diff(columns.columns(), node.columns.columns()) { it.id }
        
        // HACK
        tableColumns.value = tableColumnsOff(indexColumn, node.columns)
        tableRows.value = rowsOf(node.columns)
        columns = node.columns



    }

    private fun <V : Any> columnValue(column: Column<K, V>, index: K): ColumnValue<K, V> {
        return ColumnValue(column, column[index])
    }


    data class Row<K : Comparable<K>>(
        val index: K?,
        val values: List<ColumnValue<K, out Any>>,
        val newLine: Boolean = false
    ) {
        private val columnValueMap = values.associateBy { it.column.id to it.column.valueType }
//        private var changes = emptyList<ColumnValue<K, out Any>>()

        fun <V: Any> get(column: Column<K, V>): V? {
            val columnValue = columnValueMap[column.id to column.valueType]
            val value = (columnValue?.value) as V?
            return value
        }

        fun set(change: ColumnValue<K, out Any>): Row<K> {
             return copy(values = values.filter { it.column!=change.column } + change)
        }
    }

    data class ColumnValue<K : Comparable<K>, V : Any>(
        val column: Column<K, V>,
        val value: V?
    )
}