package de.flapdoodle.tab.ui.views.table

import de.flapdoodle.kfx.controls.bettertable.*
import de.flapdoodle.kfx.controls.bettertable.events.ReadOnlyState
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.Columns
import de.flapdoodle.tab.types.Unknown
import de.flapdoodle.tab.ui.CellFactories
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.Background
import javafx.scene.layout.StackPane

class TableViewPane<K : Comparable<K>>(
    node: de.flapdoodle.tab.model.Node.Calculated<K>,
    val context: Labels.WithContext = Labels.with(TablePane::class)
) : StackPane() {
    private val nodeId = node.id

    private var columns = node.columns

    private val tableRows = SimpleObjectProperty(rowsOf(columns))
    private val indexColumn = indexColumn(context.text("column.index","#"), node.indexType)
    private val tableColumns = SimpleObjectProperty(tableColumnsOf(indexColumn, columns))

    private val tableChangeListener: TableChangeListener<Row<K>> = object : TableChangeListener<Row<K>> {
        override fun changeCell(row: Row<K>, change: TableChangeListener.CellChange<Row<K>, out Any>): TableChangeListener.ChangedRow<Row<K>> {
            TODO("Not yet implemented")
        }

        override fun emptyRow(index: Int): Row<K> {
            return Row(null, emptyList())
        }

        override fun updateRow(
            row: Row<K>,
            changed: Row<K>,
            errors: List<TableChangeListener.CellError<Row<K>, out Any>>
        ) {
            TODO("Not yet implemented")
        }

        override fun removeRow(row: Row<K>) {
            TODO("Not yet implemented")
        }

        override fun insertRow(index: Int, row: Row<K>): Boolean {
            TODO("Not yet implemented")
        }
    }

    private val table2 = Table(
        rows = tableRows,
        columns = tableColumns,
        changeListener = tableChangeListener,
        cellFactory = CellFactories.defaultCellFactory(),
        fieldFactoryLookup = CellFactories.fieldFactoryLookup,
        stateFactory = { ReadOnlyState(it) },
        headerColumnFactory = HeaderColumnFactory.Default<Row<K>>().andThen { column, headerColumn ->
            if (column is NormalColumn<K, out Any>) {
                headerColumn.backgroundProperty().value = Background.fill(column.column.color.brighter().desaturate())
            }
            if (column is UnknownColumn<K, out Any>) {
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

    private fun tableColumnsOf(indexColumn: de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, out Any>, columns: Columns<K>): List<de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, out Any>> {
        return listOf(indexColumn) + columns.columns().map {
            column(it)
        }
    }

    private fun indexColumn(label: String, indexType: TypeInfo<K>): de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, K> {
        return IndexColumn(label, indexType)
    }

    private fun <V: Any> column(column: Column<K, V>): de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, V> {
        if (column.valueType.isAssignable(TypeInfo.of(Unknown::class.java))) {
            return UnknownColumn(column)
        } else {
            return NormalColumn(column)
        }
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
            property = ColumnProperty(indexType, { row -> row.index }),
            editable = false
    ), TableColumn<K, K> {
        override fun applyChange(row: Row<K>, change: TableChangeListener.CellChange<Row<K>, out Any>): Row<K> {
            return row.copy(index = change.value as K?)
        }
    }

    data class UnknownColumn<K: Comparable<K>, V: Any>(val column: Column<K, V>) :
        de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, V>(
            label = column.name.shortest(),
            property = ColumnProperty(TypeInfo.of(String::class.java) as TypeInfo<V>, { row -> null }),
            editable = false
        ), TableColumn<K, V> {
        override fun applyChange(row: Row<K>, change: TableChangeListener.CellChange<Row<K>, out Any>): Row<K> {
            return row.set(ColumnValue(column, change.value as V?))
        }

    }

    data class NormalColumn<K: Comparable<K>, V: Any>(val column: Column<K, V>) :
        de.flapdoodle.kfx.controls.bettertable.Column<Row<K>, V>(
            label = column.name.shortest(),
            property = ColumnProperty(column.valueType, { row -> row.get(column) }),
            editable = false
        ), TableColumn<K, V> {
        override fun applyChange(row: Row<K>, change: TableChangeListener.CellChange<Row<K>, out Any>): Row<K> {
            return row.set(ColumnValue(column, change.value as V?))
        }

    }


    init {
        children.add(table2)
    }

    fun update(node: de.flapdoodle.tab.model.Node.Calculated<K>) {
//        val columnChanges = Diff.diff(columns.columns(), node.columns.columns()) { it.id }
        
        // HACK
        tableColumns.value = tableColumnsOf(indexColumn, node.columns)
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