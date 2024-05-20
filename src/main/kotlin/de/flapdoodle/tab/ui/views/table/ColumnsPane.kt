package de.flapdoodle.tab.ui.views.table

import de.flapdoodle.kfx.controls.fields.ValidatedLabel
import de.flapdoodle.kfx.layout.grid.TableCell
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.dialogs.NewColumn
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox

class ColumnsPane<K: Comparable<K>>(
    node: de.flapdoodle.tab.model.Node.Table<K>,
    modelChangeListener: ModelChangeListener
): VBox() {
    private val nodeId = node.id

    private val valuesModel = SimpleObjectProperty(node.columns.columns())
    private val nameColumn = WeightGridTable.Column<Column<K, out Any>>(weight = 1.0, cellFactory = {
        TableCell.with(Labels.label(it.name), Column<K, out Any>::name, Label::setText)
    })
    private val actionColumn = WeightGridTable.Column<Column<K, out Any>>(weight = 0.1, cellFactory = { column ->
        TableCell(Button("-").apply {
            onAction = EventHandler {
                modelChangeListener.change(ModelChange.RemoveColumn(nodeId, column.id))
            }
        })
    })
    private val columnsPanel = WeightGridTable(
        model = valuesModel,
        indexOf = { it.id to it.valueType },
        columns = listOf(
            nameColumn,
            actionColumn
        ),
        footerFactory = { values, columns ->
            val button = Button("+").apply {
                onAction = EventHandler {
                    val column = NewColumn.open(node.indexType)
                    if (column != null) {
                        modelChangeListener.change(ModelChange.AddColumn(nodeId, column))
                    }
                }
            }
            mapOf(actionColumn to button)
        }
    )

    init {
        children.add(columnsPanel)
    }

    private fun <T: Any> typedlabelWithChangeListener(value: SingleValue<T>): Pair<javafx.scene.Node, WeightGridTable.ChangeListener<SingleValue<out Any>>> {
        val typedlabel = typedlabel(value)
        return typedlabel to WeightGridTable.ChangeListener {
            typedlabel.set(it.value as T?)
        }
    }

    private fun <T: Any> typedlabel(value: SingleValue<T>): ValidatedLabel<T> {
        return ValidatedLabel(Converters.validatingConverter(value.valueType)).apply {
            set(value.value)
        }
    }

    fun update(node: de.flapdoodle.tab.model.Node.Table<K>) {
        valuesModel.value = node.columns.columns()
    }
}