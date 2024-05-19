package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ChoiceBoxes
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.config.ValueTypes
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import de.flapdoodle.tab.ui.resources.ResourceBundles
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData
import kotlin.reflect.KClass

class ChangeValue<T: Any>(
    private val nodeId: Id<out de.flapdoodle.tab.model.Node.Constants>,
    private val value: SingleValue<T>
) : DialogContent<ModelChange>() {

    private val name = Labels.label(ChangeValue::class,"name","Name")
    private val nameField = ValidatingTextField(Converters.validatingConverter(String::class)
        .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })

    init {
        bindCss("change-value")
        
        columnWeights(1.0, 3.0)

        add(name, 0, 0)
        add(nameField, 1, 0)

        nameField.set(value.name)
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.invalidInputs(nameField)
    }

    override fun result(): ModelChange? {
        return ModelChange.ChangeValueProperties(nodeId, value.id, nameField.text)
    }

    companion object {

        fun openWith(nodeId: Id<out de.flapdoodle.tab.model.Node.Constants>, value: SingleValue<out Any>): ModelChange? {
            return DialogWrapper.open { ChangeValue(nodeId, value) }
        }
    }
}