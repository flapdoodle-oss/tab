package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ChoiceBoxes
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.config.ValueTypes
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import de.flapdoodle.tab.ui.resources.ResourceBundles
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData
import kotlin.reflect.KClass

class NewValue : DialogContent<NewValue.NewValue>() {

    private val name = Labels.label(NewValue::class,"name","Name")
    private val nameField = ValidatingTextField(Converters.validatingConverter(String::class)
        .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val type = Labels.label(NewValue::class,"type","Type")
    private val typeField = ChoiceBoxes.forTypes(
        resourceBundle = ResourceBundles.valueTypes(),
        classes = ValueTypes.all(),
    )

    init {
        bindCss("new-value")
        
        columnWeights(1.0, 3.0)

        add(name, 0, 0)
        add(nameField, 1, 0)
        add(type, 0, 1)
        add(typeField, 1, 1, HPos.LEFT)
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.invalidInputs(nameField, typeField)
    }

    override fun result(): NewValue? {
        return NewValue(nameField.text, typeField.selectionModel.selectedItem)
    }

    data class NewValue(
        val name: String,
        val type: KClass<out Any>
    )

    companion object {
        fun open(): NewValue? {
            return DialogWrapper.open { NewValue() }
        }
    }
}