package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.kfx.controls.textfields.TypedTextField
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.change.ModelChange
import de.flapdoodle.tab.app.model.data.SingleValue
import de.flapdoodle.tab.app.model.data.SingleValueId
import de.flapdoodle.tab.app.model.data.SingleValues
import de.flapdoodle.tab.app.ui.ModelChangeListener
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField

class InlineConstantUIAdapter(
    node: Node.Constants,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id
    val model = SimpleObjectProperty(node.values.values)

    val nameColumn =WeightGridTable.Column<SingleValue<out Any>>(
        weight = 1.0,
        nodeFactory = { Label(it.name) to WeightGridTable.ChangeListener { } })
    val valueColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 10.0,
        nodeFactory = { textField(nodeId, it, modelChangeListener) to WeightGridTable.ChangeListener { } })

    val content = WeightGridTable(
        model = model,
        indexOf = SingleValue<out Any>::id,
        columns = listOf(nameColumn, valueColumn),
        footerFactory = { values, columns ->
            val nameField = TextField().apply {
                prefWidth = 20.0
            }
            val button = Button("+").apply {
                maxWidth = 200.0
                onAction = EventHandler {
                    val value = SingleValue(nameField.text, Int::class)
                    modelChangeListener.change(ModelChange.AddValue(nodeId, value))
                }
            }
            mapOf(nameColumn to nameField, valueColumn to button)
        }
    )

    init {
        children.add(content)
    }

    private fun <T: Any> textField(id: Id<out Node.Constants>, value: SingleValue<T>, modelChangeListener: ModelChangeListener): TypedTextField<T> {
        return TypedTextField(value.valueType).apply {
            set(value.value)
            prefWidth = 60.0
            valueProperty().addListener { observable, oldValue, newValue ->
                modelChangeListener.change(ModelChange.ChangeValue(id, value.id, newValue))
            }
        }
    }

    override fun update(node: Node) {
        require(node is Node.Constants) { "wrong type $node" }
        model.value = node.values.values
    }
}