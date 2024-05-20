package de.flapdoodle.tab.ui.views

import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.grid.TableCell
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.ModelChangeListener
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.colors.ColorDot
import de.flapdoodle.tab.ui.views.dialogs.ChangeValue
import de.flapdoodle.tab.ui.views.dialogs.NewValue
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TextField

class ConstantUIAdapter(
    node: de.flapdoodle.tab.model.Node.Constants,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id
    val model = SimpleObjectProperty(node.values.values)

    private val nameColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        cellFactory = {
            TableCell.with(Labels.label(it.name), SingleValue<out Any>::name, Label::setText)
        })

    private val colorColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        cellFactory = {
            TableCell(ColorDot(it.color)) { c, v -> c.set(v.color) }
        })

    private val valueColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 10.0,
        cellFactory = {
            textFieldTableCell(nodeId, it, modelChangeListener) as TableCell<SingleValue<out Any>, Node>
        })

    private val changeColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        cellFactory = { value ->
            TableCell(button("change", "?") {
                val change = ChangeValue.openWith(node.id, value)
                if (change != null) modelChangeListener.change(change)
            })
        }
    )

    private val deleteColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        cellFactory = { value ->
            TableCell(button("delete", "-") {
                modelChangeListener.change(ModelChange.RemoveValue(nodeId, value.id))
            })
        }
    )

    private val content = WeightGridTable(
        model = model,
        indexOf = SingleValue<out Any>::id,
        columns = listOf(colorColumn, nameColumn, valueColumn, changeColumn, deleteColumn),
        footerFactory = { values, columns ->
            val label = Labels.label("   ")
            val field = TextField().apply {
                isEditable = false
                isDisable = true
            }

            val button = button("add", "+").apply {
//                maxWidth = 200.0
                onAction = EventHandler {
                    val newValue = NewValue.open()
                    if (newValue != null) {
                        modelChangeListener.change(
                            ModelChange.AddValue(
                                nodeId,
                                SingleValue(newValue.name, TypeInfo.of(newValue.type.javaObjectType))
                            )
                        )
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

    private fun <T : Any> textFieldTableCell(
        id: Id<out de.flapdoodle.tab.model.Node.Constants>,
        value: SingleValue<T>,
        modelChangeListener: ModelChangeListener
    ): TableCell<SingleValue<T>, ValidatingTextField<T>> {
        return TableCell.with(
            textField(id, value, modelChangeListener),
            SingleValue<T>::value,
            ValidatingTextField<T>::set
        )
    }

    private fun <T : Any> textField(
        id: Id<out de.flapdoodle.tab.model.Node.Constants>,
        value: SingleValue<T>,
        modelChangeListener: ModelChangeListener
    ): ValidatingTextField<T> {
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