package de.flapdoodle.tab.app.ui.views.table

import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.controls.bettertable.Table
import de.flapdoodle.kfx.controls.bettertable.TableChangeListener
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

class TablePane<K : Comparable<K>>(
    node: Node.Table<K>,
    val modelChangeListener: ModelChangeListener
) : StackPane() {
    private val nodeId = node.id

    // HACK HACK HACK

//    private var columns = columnsOf(node.columns)
//    private var rows = rowsOf(node.columns)
//    private var tempRowIndexMap = emptyMap<Int, K>()

//    private val indexColumn = TableColumn<Row<K>, K>("index").apply {
//        isEditable = true
//        cellValueFactory =
//            Callback { param -> if (param.value != null) SimpleObjectProperty(param.value.index) else SimpleObjectProperty() }
//        cellFactory = TextFieldTableCell.forTableColumn(Converters.converterFor(node.indexType) as StringConverter<K>)
//        onEditCommit = EventHandler { event ->
//            val row = event.rowValue
//            if (row.newLine) {
//                println("change index only")
//                row.index = event.newValue
//                modelChangeListener.change(ModelChange.SetColumns(nodeId,event.newValue, changes = row.changes().map { change ->
//                    change.column.id to change.value
//                }))
//            } else {
//                println("change index for all values from ${row.index} to ${event.newValue}")
//                modelChangeListener.change(ModelChange.MoveValues(nodeId, event.oldValue, event.newValue))
//            }
//        }
//    }
//    private val table = TableView<Row<K>>().apply {
//        isEditable = true
//        prefHeight = 200.0
//    }

    private var columns = node.columns

    private val tableRows = SimpleObjectProperty(rowsOf(columns))
    private val tableColumns = SimpleObjectProperty(tableColumnsOff(node.indexType, columns))

    private val tableChangeListener: TableChangeListener<Row<K>> = object : TableChangeListener<Row<K>> {
        override fun changeCell(row: Row<K>, change: TableChangeListener.CellChange<Row<K>, out Any>): Row<K> {
//            return row.set(asColumnValue(change))
            return (change.column as TableColumn<K, out Any>).applyChange(row, change)
        }

        override fun emptyRow(index: Int): Row<K> {
            return Row(null, emptyList())
        }

        override fun updateRow(row: Row<K>, changed: Row<K>) {
            val changes = Diff.diff(row.values, changed.values) { it.column }
            val removed = changes.removed.map { it.column.id to null }
            val modified = changes.changed.map { it.first.column.id to it.second.value }
            val added = changes.new.map { it.column.id to it.value }

            modelChangeListener.change(ModelChange.SetColumns(nodeId,row.index!!, changes = removed + modified + added))
        }

        override fun removeRow(row: Row<K>) {
            TODO("Not yet implemented")
        }

        override fun insertRow(index: Int, row: Row<K>) {
            modelChangeListener.change(ModelChange.SetColumns(nodeId,row.index!!, changes = row.values.map { change ->
                change.column.id to change.value
            }))
        }

    }

    private val table2 = Table(tableRows, tableColumns, tableChangeListener)

    private fun rowsOf(columns: Columns<K>): List<Row<K>> {
        return columns.index().map { index ->
            Row(index, columns.columns().map { c ->
                columnValue(c, index)
            })
        }
    }

    private fun tableColumnsOff(indexType: KClass<K>,  columns: Columns<K>): List<de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, out Any>> {
        return listOf(indexColumn(indexType)) + columns.columns().map {
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
            editable = true,
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
            editable = true,
            converter = Converters.converterFor(column.valueType)
        ), TableColumn<K, V> {
        override fun applyChange(row: Row<K>, change: TableChangeListener.CellChange<Row<K>, out Any>): Row<K> {
            return row.set(ColumnValue(column, change.value as V?))
        }

    }


    init {
        children.add(table2)
//        children.add(table)
//        if (columns.isNotEmpty()) {
//            table.columns.add(indexColumn)
//            table.columns.addAll(columns.map { it.tableColumn })
//        }
//        table.items.addAll(rows)
//        table.items.add(Row(null, emptyList(), true))
    }

    fun update(node: Node.Table<K>) {
        // HACK
        tableRows.value = rowsOf(node.columns)
        tableColumns.value = tableColumnsOff(node.indexType, node.columns)
        columns = node.columns
                       
//        val oldColumns = columns
//        val newColumns = columnsOf(node.columns)
//        val oldRows = rows
//        val newRows = rowsOf(node.columns)
//
//        // HACK
//        table.columns.clear()
//        table.items.clear()
//        if (newColumns.isNotEmpty()) {
//            table.columns.add(indexColumn)
//            table.columns.addAll(newColumns.map { it.tableColumn })
//        }
//        table.items.addAll(newRows)
////        table.items.add(Row(null, emptyList(), true))
//
//        tempRowIndexMap = emptyMap()
//        columns = newColumns
//        rows = newRows
    }




















//    private fun columnsOf(columns: Columns<K>): List<RowColumn<K, out Any>> {
//        return columns.columns().map { rowColumn(it) }
//    }

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

        fun <V : Any> valueAdapterOf(column: Column<K, V>): ObservableValue<V> {
            val columnValue = columnValueMap[column.id]
            val value = (columnValue?.value) as V?
            return SimpleObjectProperty<V>(value)
        }

        fun <V: Any> get(column: Column<K, V>): V? {
            val columnValue = columnValueMap[column.id]
            val value = (columnValue?.value) as V?
            return value
        }

        fun set(change: ColumnValue<K, out Any>): Row<K> {
             return copy(values = values.filter { it.column!=change.column } + change)
        }

//        fun changes() = changes
    }

//    fun <V : Any>rowColumn(column: Column<K, V>): RowColumn<K, V> {
//        return RowColumn(column, tableColumnOf(column))
//    }



//    data class RowColumn<K : Comparable<K>, V : Any>(
//        val column: Column<K, V>,
//        val tableColumn: TableColumn<Row<K>, V>
//    ) {
//        fun id() = column.id
//    }

    data class ColumnValue<K : Comparable<K>, V : Any>(
        val column: Column<K, V>,
        val value: V?
    )

//    private fun <K : Comparable<K>, V : Any> tableColumnOf(column: Column<K, V>): TableColumn<Row<K>, V> {
//        return TableColumn<Row<K>, V>(column.name).apply {
//            isSortable = false
//            cellValueFactory = Callback { param ->
//                if (param.value != null) {
//                    param.value.valueAdapterOf(column)
//                } else {
//                    SimpleObjectProperty()
//                }
//            }
//            cellFactory = TextFieldTableCell.forTableColumn<Row<K>?, V>(Converters.converterFor(column.valueType))
//            onEditCommit = EventHandler {
//                val row = it.rowValue
//                if (row.newLine) {
//                    println("new line")
//                    val index = row.index
//                    if (index!=null) {
//                        modelChangeListener.change(ModelChange.SetColumns(nodeId, index, listOf(column.id to it.newValue)))
//                    } else {
//                        row.set(ColumnValue(column, it.newValue))
//                    }
//                } else {
//                    println("change line")
//                    val index = requireNotNull(row.index) {"index is null"}
//                    modelChangeListener.change(ModelChange.SetColumns(nodeId, index, listOf(column.id to it.newValue)))
//                }
//            }
//        }
//    }
}