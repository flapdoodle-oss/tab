package de.flapdoodle.tab.app.ui.views.table

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

    // HACK HACK HACK

    private var columns = columnsOf(node.columns)
    private var rows = rowsOf(node.columns)
    private var tempRowIndexMap = emptyMap<Int, K>()

    private val indexColumn = TableColumn<Row<K>, K>("index").apply {
        isEditable = true
        cellValueFactory =
            Callback { param -> if (param.value != null) SimpleObjectProperty(param.value.index) else SimpleObjectProperty() }
        cellFactory = TextFieldTableCell.forTableColumn(Converters.converterFor(node.indexType) as StringConverter<K>)
        onEditCommit = EventHandler { event ->
            val row = event.rowValue
            if (row.newLine) {
                println("change index only")
                row.index = event.newValue
                modelChangeListener.change(ModelChange.SetColumns(nodeId,event.newValue, changes = row.changes().map { change ->
                    change.column.id to change.value
                }))
            } else {
                println("change index for all values from ${row.index} to ${event.newValue}")
                modelChangeListener.change(ModelChange.MoveValues(nodeId, event.oldValue, event.newValue))
            }
        }
    }
    private val table = TableView<Row<K>>().apply {
        isEditable = true
        prefHeight = 200.0
    }

    init {
        children.add(table)
        if (columns.isNotEmpty()) {
            table.columns.add(indexColumn)
            table.columns.addAll(columns.map { it.tableColumn })
        }
        table.items.addAll(rows)
        table.items.add(Row(null, emptyList(), true))
    }

    fun update(node: Node.Table<K>) {
        val oldColumns = columns
        val newColumns = columnsOf(node.columns)
        val oldRows = rows
        val newRows = rowsOf(node.columns)

//        val columnChange = Diff.between(oldColumns, newColumns, RowColumn<K, out Any>::id)
//        val rowChange = Diff.between(oldRows, newRows, Row<K>::index)

//        println("columnChange: $columnChange")
//        println("rowChange: $rowChange")

        // HACK
        table.columns.clear()
        table.items.clear()
        if (newColumns.isNotEmpty()) {
            table.columns.add(indexColumn)
            table.columns.addAll(newColumns.map { it.tableColumn })
        }
        table.items.addAll(newRows)
        table.items.add(Row(null, emptyList(), true))

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
        return columns.columns().map { rowColumn(it) }
    }

    private fun <V : Any> columnValue(column: Column<K, V>, index: K): ColumnValue<K, V> {
        return ColumnValue(column, column[index])
    }


    data class Row<K : Comparable<K>>(
        var index: K?,
        val values: List<ColumnValue<K, out Any>>,
        val newLine: Boolean = false
    ) {
        private val columnValueMap = values.associateBy { it.column.id }
        private var changes = emptyList<ColumnValue<K, out Any>>()

        fun <V : Any> valueAdapterOf(column: Column<K, V>): ObservableValue<V> {
            val columnValue = columnValueMap[column.id]
            val value = (columnValue?.value) as V?
            return SimpleObjectProperty<V>(value)
        }

        fun set(change: ColumnValue<K, out Any>) {
            changes = changes.filter { it.column!=change.column } + change
        }

        fun changes() = changes
    }

    fun <V : Any>rowColumn(column: Column<K, V>): RowColumn<K, V> {
        return RowColumn(column, tableColumnOf(column))
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

    private fun <K : Comparable<K>, V : Any> tableColumnOf(column: Column<K, V>): TableColumn<Row<K>, V> {
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
                val row = it.rowValue
                if (row.newLine) {
                    println("new line")
                    val index = row.index
                    if (index!=null) {
                        modelChangeListener.change(ModelChange.SetColumns(nodeId, index, listOf(column.id to it.newValue)))
                    } else {
                        row.set(ColumnValue(column, it.newValue))
                    }
                } else {
                    println("change line")
                    val index = requireNotNull(row.index) {"index is null"}
                    modelChangeListener.change(ModelChange.SetColumns(nodeId, index, listOf(column.id to it.newValue)))
                }
            }
        }
    }
}