package de.flapdoodle.tab.ui.views.table

import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.grid.TableCell
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.ui.ChangeListener
import de.flapdoodle.tab.ui.resources.Buttons
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.colors.ColorDot
import de.flapdoodle.tab.ui.views.dialogs.ChangeColumn
import de.flapdoodle.tab.ui.views.dialogs.NewColumn
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.StackPane

class ColumnsPane<K: Comparable<K>>(
    node: Node.Table<K>,
    changeListener: ChangeListener
): StackPane() {
    private val nodeId = node.id
    private val context = Labels.with(ColumnsPane::class)

    private val valuesModel = SimpleObjectProperty(node.columns.columns())

    private val colorColumn = WeightGridTable.Column<Column<K, out Any>>(
        weight = 0.0,
        cellFactory = {
            TableCell(ColorDot(it.color)) { c, v -> c.set(v.color) }
        })


    private val nameColumn = WeightGridTable.Column<Column<K, out Any>>(weight = 1.0, horizontalPosition = HPos.LEFT, cellFactory = {
        TableCell.with(Labels.label(it.name.long), { it.name.long }, Label::setText)
    })
    private val interpolationColumn = WeightGridTable.Column<Column<K, out Any>>(weight = 0.0, cellFactory = {
        TableCell(Labels.label(it.interpolationType.name)) { l, v -> l.text = v.interpolationType.name}
    })
    private val changeColumn = WeightGridTable.Column<Column<K, out Any>>(weight = 0.0, cellFactory = { column ->
        TableCell.with<Column<K, out Any>, Button, EventHandler<ActionEvent>>(Buttons.change(context), { v -> EventHandler {
            val change = ChangeColumn.open(nodeId, v)
            if (change!=null) {
                changeListener.change(change)
            }
        }}, Button::setOnAction).apply {
            updateCell(column)
        }
    })
    private val deleteColumn = WeightGridTable.Column<Column<K, out Any>>(weight = 0.0, cellFactory = { column ->
        TableCell(Buttons.delete(context) {
            changeListener.change(Change.Table.RemoveColumn(nodeId, column.id))
        })
    })
    private val columnsPanel = WeightGridTable(
        model = valuesModel,
        indexOf = { it.id to it.valueType },
        columns = listOf(
            colorColumn,
            nameColumn,
            interpolationColumn,
            changeColumn,
            deleteColumn
        ),
        footerFactory = { values, columns ->
            val button = Buttons.add(context, ) {
                val column = NewColumn.open(node.indexType)
                if (column != null) {
                    changeListener.change(Change.Table.AddColumn(nodeId, column))
                }
            }
            mapOf(deleteColumn to button)
        }
    ).apply {
        cssClassName("table")
    }

    init {
        cssClassName("columns")
        children.add(columnsPanel)
    }

    fun update(node: Node.Table<K>) {
        valuesModel.value = node.columns.columns()
    }
}