package de.flapdoodle.tab.ui.views

import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.views.colors.ColorDot
import de.flapdoodle.tab.ui.views.dialogs.ChangeValue
import de.flapdoodle.tab.ui.views.dialogs.NewValue
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.control.TextField

class ConstantUIAdapter(
    node: de.flapdoodle.tab.model.Node.Constants,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id
    val model = SimpleObjectProperty(node.values.values)

    val nameColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        nodeFactory = {
            val label = Label(it.name).apply {
                minWidth = USE_PREF_SIZE
            }
            label to WeightGridTable.ChangeListener { label.text = it.name }
        })
    
    val colorColumn =WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        nodeFactory = { ColorDot(it.color) to WeightGridTable.ChangeListener { } })
    val valueColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 10.0,
        nodeFactory = { val textField = textField(nodeId, it, modelChangeListener)
            textField to WeightGridTable.ChangeListener {
                //textField.set(it.value as Nothing?)
            } })

    val changeColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        nodeFactory = { value ->
            val button = button("change","?").apply {
                onAction = EventHandler {
                    val change = ChangeValue.openWith(node.id, value)
                    if (change!=null) modelChangeListener.change(change)
                }
            }
            button to WeightGridTable.ChangeListener {  }
        }
    )

    val deleteColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        nodeFactory = { value ->
            val button = button("delete","-").apply {
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
        columns = listOf(colorColumn, nameColumn, valueColumn, changeColumn, deleteColumn),
        footerFactory = { values, columns ->
            val label = Label("   ").apply {
                minWidth = USE_PREF_SIZE
            }
            val field = TextField().apply {
                isEditable = false
                isDisable = true
            }

            val button = button("add","+").apply {
//                maxWidth = 200.0
                onAction = EventHandler {
                    val newValue = NewValue.open()
                    if (newValue!=null) {
                        modelChangeListener.change(ModelChange.AddValue(nodeId, SingleValue(newValue.name, TypeInfo.of(newValue.type.javaObjectType))))
                    }
                }
            }
            mapOf(
                nameColumn to label,
                valueColumn to field,
                deleteColumn to button
            )
        }
    ).apply {
        cssClassName("table")
//        verticalSpace().value = 10.0
//        horizontalSpace().value = 10.0
    }

    init {
        bindCss("constants-ui")
        children.add(content)
    }

    private fun <T: Any> textField(id: Id<out de.flapdoodle.tab.model.Node.Constants>, value: SingleValue<T>, modelChangeListener: ModelChangeListener): ValidatingTextField<T> {
        val converter = Converters.validatingConverter(value.valueType)
        return ValidatingTextField(converter).apply {
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