package de.flapdoodle.tab.ui.views

import de.flapdoodle.kfx.controls.fields.TypedTextField
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.views.dialogs.NewValueDialog
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Label

class InlineConstantUIAdapter(
    node: de.flapdoodle.tab.model.Node.Constants,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id
    val model = SimpleObjectProperty(node.values.values)

    val nameColumn =WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        nodeFactory = { Label(it.name).apply {
            minWidth = USE_PREF_SIZE
        } to WeightGridTable.ChangeListener { } })
    val valueColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 10.0,
        nodeFactory = { textField(nodeId, it, modelChangeListener) to WeightGridTable.ChangeListener { } })

    val actionColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        nodeFactory = { value ->
            val button = Button("-").apply {
                onAction = EventHandler {
                    modelChangeListener.change(ModelChange.RemoveValue(nodeId, value.id))
                }
            }
            button to WeightGridTable.ChangeListener {  }
        }
    )

    val content = WeightGridTable(
        model = model,
        indexOf = SingleValue<out Any>::id,
        columns = listOf(nameColumn, valueColumn, actionColumn),
        footerFactory = { values, columns ->
            val button = Button("+").apply {
//                maxWidth = 200.0
                onAction = EventHandler {
                    val newValue = NewValueDialog.open()
                    if (newValue!=null) {
                        modelChangeListener.change(ModelChange.AddValue(nodeId, SingleValue(newValue.name, newValue.type)))
                    }
                }
            }
            mapOf(actionColumn to button)
        }
    ).apply {
        verticalSpace().value = 10.0
        horizontalSpace().value = 10.0
    }

    init {
        children.add(content)
    }

    private fun <T: Any> textField(id: Id<out de.flapdoodle.tab.model.Node.Constants>, value: SingleValue<T>, modelChangeListener: ModelChangeListener): TypedTextField<T> {
        return TypedTextField(value.valueType).apply {
            set(value.value)
            prefWidth = 60.0
            valueProperty().addListener { observable, oldValue, newValue ->
                modelChangeListener.change(ModelChange.ChangeValue(id, value.id, newValue))
            }
        }
    }

    override fun update(node: de.flapdoodle.tab.model.Node) {
        require(node is de.flapdoodle.tab.model.Node.Constants) { "wrong type $node" }
        model.value = node.values.values
    }
}