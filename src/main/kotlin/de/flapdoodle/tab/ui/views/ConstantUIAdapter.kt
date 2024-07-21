package de.flapdoodle.tab.ui.views

import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.css.bindCss
import de.flapdoodle.kfx.css.cssClassName
import de.flapdoodle.kfx.layout.grid.TableCell
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.ui.ChangeListener
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Buttons
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.colors.ColorDot
import de.flapdoodle.tab.ui.views.common.DescriptionPane
import de.flapdoodle.tab.ui.views.dialogs.ChangeValue
import de.flapdoodle.tab.ui.views.dialogs.NewValue
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.HPos
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.layout.VBox

class ConstantUIAdapter(
    node: de.flapdoodle.tab.model.Node.Constants,
    val changeListener: ChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id
    val model = SimpleObjectProperty(node.values.values)

    private val context = Labels.with(ConstantUIAdapter::class)

    private val description = DescriptionPane(node.name.description)

    private val nameColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        horizontalPosition = HPos.LEFT,
        cellFactory = { value ->
            Labels.tableCell(value) { it.name.long }
        })

    private val colorColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        cellFactory = {
            ColorDot.tableCell(it, SingleValue<out Any>::color)
        })

    private val valueColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 10.0,
        cellFactory = {
            textFieldTableCell(nodeId, it, changeListener) as TableCell<SingleValue<out Any>, Node>
        })

    private val changeColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        cellFactory = { value ->
            Buttons.tableCell(value, Buttons.change(context)) {
                val change = ChangeValue.openWith(node.id, it)
                if (change != null) changeListener.change(change)
            }
        }
    )

    private val deleteColumn = WeightGridTable.Column<SingleValue<out Any>>(
        weight = 0.0,
        cellFactory = { value ->
            Buttons.tableCell(value, Buttons.delete(context)) {
                changeListener.change(Change.Constants.RemoveValue(nodeId, it.id))
            }
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
                    changeListener.change(
                        Change.Constants.AddValue(
                            nodeId,
                            SingleValue(newValue.name, TypeInfo.of(newValue.type.javaObjectType), color = newValue.color)
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
        modelChangeListener: ChangeListener
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
        modelChangeListener: ChangeListener
    ): ValidatingTextField<T> {
        val converter = Converters.validatingConverter(value.valueType)
        return ValidatingTextField(converter).apply {
            set(value.value)
            prefWidth = 60.0
            valueProperty().addListener { observable, oldValue, newValue ->
                modelChangeListener.change(Change.Constants.ChangeValue(id, value.id, newValue))
            }
        }
    }

    override fun update(node: de.flapdoodle.tab.model.Node) {
        require(node is de.flapdoodle.tab.model.Node.Constants) { "wrong type $node" }
        model.value = node.values.values
        description.update(node.name.description)
    }
}