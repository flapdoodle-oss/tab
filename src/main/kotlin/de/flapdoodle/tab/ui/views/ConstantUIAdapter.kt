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
import de.flapdoodle.tab.ui.resources.Buttons
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.colors.ColorDot
import de.flapdoodle.tab.ui.views.common.DescriptionPane
import de.flapdoodle.tab.ui.views.dialogs.ChangeValue
import de.flapdoodle.tab.ui.views.dialogs.NewValue
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.VBox

class ConstantUIAdapter(
    node: de.flapdoodle.tab.model.Node.Constants,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id
    val model = SimpleObjectProperty(node.values.values)

    private val context = Labels.with(ConstantUIAdapter::class)

    private val description = DescriptionPane(node.name.description)

    private val nameColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        horizontalPosition = HPos.LEFT,
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
            TableCell.with<SingleValue<out Any>, Button, EventHandler<ActionEvent>>(Buttons.change(context), { v -> EventHandler {
                val change = ChangeValue.openWith(node.id, v)
                if (change != null) modelChangeListener.change(change)
            }}, Button::setOnAction).apply {
                updateCell(value)
            }
        }
    )

    private val deleteColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        cellFactory = { value ->
            TableCell(Buttons.delete(context) {
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

            val button = Buttons.add(context) {
//                maxWidth = 200.0
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
        children.add(VBox().also { vbox ->
            vbox.children.add(description)
            vbox.children.add(content)
        })
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
        description.update(node.name.description)
    }
}