package de.flapdoodle.tab.ui.views.table

import de.flapdoodle.kfx.layout.grid.TableCell
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.ui.ModelChangeListener
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
    modelChangeListener: ModelChangeListener
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
        TableCell.with(Labels.label(it.name), Column<K, out Any>::name, Label::setText)
    })
    private val interpolationColumn = WeightGridTable.Column<Column<K, out Any>>(weight = 0.0, cellFactory = {
        TableCell(Labels.label(it.interpolationType.name)) { l, v -> l.text = v.interpolationType.name}
    })
    private val changeColumn = WeightGridTable.Column<Column<K, out Any>>(weight = 0.0, cellFactory = { column ->
        TableCell.with<Column<K, out Any>, Button, EventHandler<ActionEvent>>(Buttons.change(context), { v -> EventHandler {
            val change = ChangeColumn.open(nodeId, v)
            if (change!=null) {
                modelChangeListener.change(change)
            }
        }}, Button::setOnAction).apply {
            updateCell(column)
        }
    })
    private val deleteColumn = WeightGridTable.Column<Column<K, out Any>>(weight = 0.0, cellFactory = { column ->
        TableCell(Buttons.delete(context) {
            modelChangeListener.change(ModelChange.RemoveColumn(nodeId, column.id))
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
                    modelChangeListener.change(ModelChange.AddColumn(nodeId, column))
                }
            }
            mapOf(deleteColumn to button)
        }
    )

    init {
        children.add(columnsPanel)
    }

    fun update(node: Node.Table<K>) {
        valuesModel.value = node.columns.columns()
    }
}