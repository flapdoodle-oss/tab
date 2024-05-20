package de.flapdoodle.tab.ui.views.table

import de.flapdoodle.kfx.layout.grid.TableCell
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.resources.Buttons
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.dialogs.ChangeColumn
import de.flapdoodle.tab.ui.views.dialogs.NewColumn
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.StackPane

class ColumnsPane<K: Comparable<K>>(
    node: de.flapdoodle.tab.model.Node.Table<K>,
    modelChangeListener: ModelChangeListener
): StackPane() {
    private val nodeId = node.id
    private val context = Labels.with(ColumnsPane::class)

    private val valuesModel = SimpleObjectProperty(node.columns.columns())
    private val nameColumn = WeightGridTable.Column<Column<K, out Any>>(weight = 1.0, cellFactory = {
        TableCell.with(Labels.label(it.name), Column<K, out Any>::name, Label::setText)
    })
    private val changeColumn = WeightGridTable.Column<Column<K, out Any>>(weight = 0.0, cellFactory = { column ->
        TableCell(Buttons.change(context) {
            val change = ChangeColumn.open(nodeId, column)
            if (change!=null) {
                modelChangeListener.change(change)
            }
        })
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
            nameColumn,
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

    fun update(node: de.flapdoodle.tab.model.Node.Table<K>) {
        valuesModel.value = node.columns.columns()
    }
}