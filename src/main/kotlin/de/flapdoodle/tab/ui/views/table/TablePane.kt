package de.flapdoodle.tab.ui.views.table

import de.flapdoodle.kfx.controls.bettertable.ColumnProperty
import de.flapdoodle.kfx.controls.bettertable.HeaderColumnFactory
import de.flapdoodle.kfx.controls.bettertable.Table
import de.flapdoodle.kfx.controls.bettertable.TableChangeListener
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.Columns
import de.flapdoodle.tab.model.diff.Diff
import de.flapdoodle.tab.ui.ChangeListener
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.Background
import javafx.scene.layout.StackPane
import java.util.*

class TablePane<K : Comparable<K>>(
    node: Node.Table<K>,
    val changeListener: ChangeListener,
    val context: Labels.WithContext = Labels.with(TablePane::class)
) : StackPane() {
    private val nodeId = node.id         

    private var columns = node.columns

    private val tableRows = SimpleObjectProperty(rowsOf(columns))
    private val tableColumns = SimpleObjectProperty(tableColumnsOf(node.indexType, columns))

    private val tableChangeListener: TableChangeListener<Row<K>> = object : TableChangeListener<Row<K>> {
        override fun changeCell(row: Row<K>, change: TableChangeListener.CellChange<Row<K>, out Any>): TableChangeListener.ChangedRow<Row<K>> {
            return TableChangeListener.ChangedRow((change.column as TableColumn<K, out Any>).applyChange(row, change))
        }

        override fun emptyRow(index: Int): Row<K> {
            return Row(null, emptyList())
        }

        override fun updateRow(row: Row<K>, changed: Row<K>, errors: List<TableChangeListener.CellError<Row<K>, out Any>>) {
            if (row.index == changed.index) {
                val changes = Diff.diff(row.values, changed.values) { it.column }
                val removed = changes.removed.map { it.column.id to null }
                val modified = changes.changed.map { it.first.column.id to it.second.value }
                val added = changes.new.map { it.column.id to it.value }

                changeListener.change(
                    Change.Table.SetColumns(
                        nodeId,
                        row.index!!,
                        changes = removed + modified + added
                    )
                )
            } else {
                changeListener.change(
                    Change.Table.MoveValues(
                        nodeId,
                        row.index!!,
                        changed.index!!
                    )
                )
            }
        }

        override fun removeRow(row: Row<K>) {
            changeListener.change(
                Change.Table.RemoveValues(
                    nodeId,
                    row.index!!
                )
            )
        }

        override fun insertRow(index: Int, row: Row<K>): Boolean {
            return if (row.index!=null) {
                changeListener.change(
                    Change.Table.SetColumns(
                        nodeId,
                        row.index,
                        changes = row.values.map { change ->
                            change.column.id to change.value
                        })
                )
                true
            } else
                false
        }

    }

    private val table = Table(
        rows = tableRows,
        columns = tableColumns,
        changeListener = tableChangeListener,
        headerColumnFactory = HeaderColumnFactory.Default<Row<K>>().andThen { column, headerColumn ->
            if (column is NormalColumn<K, out Any>) {
                headerColumn.backgroundProperty().value = Background.fill(column.column.color.brighter().desaturate())
            }
        },
        footerColumnFactory = null
    )

    private fun rowsOf(columns: Columns<K>): List<Row<K>> {
        return columns.index().map { index ->
            Row(index, columns.columns().map { c ->
                columnValue(c, index)
            })
        }
    }

    private fun tableColumnsOf(indexType: TypeInfo<K>, columns: Columns<K>): List<de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, out Any>> {
        return listOf(indexColumn(context.text("column.index","#"), indexType)) + columns.columns().map {
            column(it)
        }
    }

    private fun indexColumn(label: String, indexType: TypeInfo<K>): de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, K> {
        return IndexColumn(label, indexType)
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

    data class IndexColumn<K: Comparable<K>>(override val label: String, val indexType: TypeInfo<K>):
        de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, K>(
            label = label,
            property = ColumnProperty(indexType,{ row -> row.index }, Converters.validatingConverter(indexType, Locale.getDefault())),
            editable = true
    ), TableColumn<K, K> {
        override fun applyChange(row: Row<K>, change: TableChangeListener.CellChange<Row<K>, out Any>): Row<K> {
            return row.copy(index = change.value as K?)
        }
    }

    data class NormalColumn<K: Comparable<K>, V: Any>(val column: Column<K, V>) :
        de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, V>(
            label = column.name.shortest(),
            property = ColumnProperty(column.valueType, { row -> row.get(column) }, Converters.validatingConverter(column.valueType, Locale.getDefault())),
            editable = true
        ), TableColumn<K, V> {
            init {

            }
        override fun applyChange(row: Row<K>, change: TableChangeListener.CellChange<Row<K>, out Any>): Row<K> {
            return row.set(ColumnValue(column, change.value as V?))
        }

    }


    init {
        children.add(table)
    }

    fun update(node: de.flapdoodle.tab.model.Node.Table<K>) {
        // HACK
        tableRows.value = rowsOf(node.columns)
        tableColumns.value = tableColumnsOf(node.indexType, node.columns)
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
        private val columnValueMap = values.associateBy { it.column.id }
//        private var changes = emptyList<ColumnValue<K, out Any>>()

        fun <V: Any> get(column: Column<K, V>): V? {
            val columnValue = columnValueMap[column.id]
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