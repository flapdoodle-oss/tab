package de.flapdoodle.tab.app.ui.views.table

import de.flapdoodle.kfx.collections.Diff
import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.change.ModelChange
import de.flapdoodle.tab.app.model.data.Column
import de.flapdoodle.tab.app.model.data.Columns
import de.flapdoodle.tab.app.ui.ModelChangeListener
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.StackPane
import javafx.util.Callback
import javafx.util.StringConverter

class TablePane<K : Comparable<K>>(
    node: Node.Table<K>,
    val modelChangeListener: ModelChangeListener
) : StackPane() {
    private val nodeId = node.id

    private var columns = columnsOf(node.columns)
    private var rows = rowsOf(node.columns)
    private var tempRowIndexMap = emptyMap<Int, K>()
    private val indexColumn = TableColumn<Row<K>, K>("index").apply {
        isEditable = true
        cellValueFactory = Callback { param ->
            if (param.value!=null) {
                SimpleObjectProperty(param.value.index)
            } else {
                SimpleObjectProperty<K>()
            }
        }
        cellFactory = TextFieldTableCell.forTableColumn(Converters.converterFor(node.indexType) as StringConverter<K>)
        onEditCommit = EventHandler {
            if (it.rowValue==null) {
                // new row
            } else {
                // change index for all values
            }

            tempRowIndexMap = tempRowIndexMap + (it.tablePosition.row to it.newValue)
        }
    }
    private val table = TableView<Row<K>>().apply {
        isEditable = true
        maxHeight = 100.0
    }

    init {
        children.add(table)
        table.columns.add(indexColumn)
        table.columns.addAll(columns.map { it.tableColumn })
        table.items.addAll(rows)
        table.items.add(null)
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
        table.columns.add(indexColumn)
        table.columns.addAll(newColumns.map { it.tableColumn })
        table.items.addAll(newRows)
        table.items.add(null)

        tempRowIndexMap = emptyMap()
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
        return columns.columns().map { rowColumn(it) { row ->
            var index = tempRowIndexMap[row]
            if (index==null) {
                if (row < rows.size) {
                    index = rows[row].index
                }
            }
            index
        } }
    }

    private fun <V : Any> columnValue(column: Column<K, V>, index: K): ColumnValue<K, V> {
        return ColumnValue(column, column[index])
    }


    data class Row<K : Comparable<K>>(val index: K, val values: List<ColumnValue<K, out Any>>) {
        private val columnValueMap = values.associateBy { it.column.id }

        fun <V : Any> valueAdapterOf(column: Column<K, V>): ObservableValue<V> {
            val columnValue = columnValueMap[column.id]
            val value = (columnValue?.value) as V?
            return SimpleObjectProperty<V>(value)
        }
    }

    fun <V : Any>rowColumn(column: Column<K, V>, indexOfRow: (Int) -> K?): RowColumn<K, V> {
        return RowColumn(column, tableColumnOf(indexOfRow, column))
    }

    data class RowColumn<K : Comparable<K>, V : Any>(
        val column: Column<K, V>,
        val tableColumn: TableColumn<Row<K>, V>
    ) {
        fun id() = column.id
    }

    data class ColumnValue<K : Comparable<K>, V : Any>(
        val column: Column<K, V>,
        val value: V?
    )

    private fun <K : Comparable<K>, V : Any> tableColumnOf(indexOfRow: (Int) -> K?, column: Column<K, V>): TableColumn<Row<K>, V> {
        return TableColumn<Row<K>, V>(column.name).apply {
            isSortable = false
            cellValueFactory = Callback { param ->
                if (param.value != null) {
                    param.value.valueAdapterOf(column)
                } else {
                    SimpleObjectProperty()
                }
            }
            cellFactory = TextFieldTableCell.forTableColumn<Row<K>?, V>(Converters.converterFor(column.valueType))
            onEditCommit = EventHandler {
                val index = indexOfRow(it.tablePosition.row)
                if (index!=null) {
                    modelChangeListener.change(ModelChange.SetColumn(nodeId, column.id, index, it.newValue))
                }
            }
        }
    }
}