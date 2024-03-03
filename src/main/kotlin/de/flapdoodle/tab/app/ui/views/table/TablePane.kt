package de.flapdoodle.tab.app.ui.views.table

import de.flapdoodle.kfx.collections.Diff
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.data.Column
import de.flapdoodle.tab.app.model.data.Columns
import de.flapdoodle.tab.app.ui.ModelChangeListener
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.VBox

class TablePane<K: Comparable<K>>(
    node: Node.Table<K>,
    modelChangeListener: ModelChangeListener
): VBox() {
    private val nodeId = node.id

    private var columns = columnsOf(node.columns)
    private var rows = rowsOf(node.columns)
    private val table = TableView<Row<K>>()

    init {
        children.add(table)
        table.columns.addAll(columns.map { it.tableColumn })
        table.items.addAll(rows)
    }

    fun update(node: Node.Table<K>) {
        val oldColumns = columns
        val newColumns = columnsOf(node.columns)
        val oldRows = rows
        val newRows = rowsOf(node.columns)

        val columnChange = Diff.between(oldColumns, newColumns, RowColumn<K, out Any>::id)
        val rowChange = Diff.between(oldRows, newRows, Row<K>::index)

//        println("columnChange: $columnChange")
//        println("rowChange: $rowChange")

        // HACK
        table.columns.clear()
        table.items.clear()
        table.columns.addAll(newColumns.map { it.tableColumn })
        table.items.addAll(newRows)

        columns = newColumns
        rows = newRows
    }

    private fun rowsOf(columns: Columns<K>): List<Row<K>> {
        return columns.index().map { index ->
            Row(index, columns.columns().map { c ->
                columnValue(c, index)
            })
        }
    }

    private fun columnsOf(columns: Columns<K>): List<RowColumn<K, out Any>> {
        return columns.columns().map { RowColumn(it) }
    }

    private fun <V: Any> columnValue(column: Column<K, V>, index: K): ColumnValue<K, V> {
        return ColumnValue(column, column[index])
    }


    data class Row<K: Comparable<K>>(val index: K, val values: List<ColumnValue<K, out Any>>)
    data class RowColumn<K: Comparable<K>, V: Any>(
        val column: Column<K, V>,
        val tableColumn: TableColumn<Row<K>, V> = tableColumnOf(column)
    ) {
        fun id() = column.id

    }

    data class ColumnValue<K: Comparable<K>, V: Any>(
        val column: Column<K, V>,
        val value: V?
    )

    companion object {
        private fun <K: Comparable<K>, V: Any> tableColumnOf(column: Column<K, V>): TableColumn<Row<K>, V> {
            return TableColumn<Row<K>, V>(column.name).apply {
                isSortable = false

            }
        }
    }
}