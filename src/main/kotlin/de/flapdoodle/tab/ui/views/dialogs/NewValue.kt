package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ChoiceBoxes
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.tab.config.ValueTypes
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.dialogs.DialogContent
import de.flapdoodle.tab.ui.dialogs.DialogWrapper
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import de.flapdoodle.tab.ui.resources.ResourceBundles
import de.flapdoodle.tab.ui.views.common.HashedColorPicker
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.scene.paint.Color
import kotlin.reflect.KClass

class NewValue : DialogContent<NewValue.NewValue>() {

    private val name = Labels.label(NewValue::class,"name","Name")
    private val nameField = ValidatingTextField(Converters.validatingConverter(String::class)
        .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val short = Labels.label(NewValues::class,"shortName","Short")
    private val shortField = ValidatingTextField(converter = Converters.validatingConverter(String::class))
    private val type = Labels.label(NewValue::class,"type","Type")
    private val typeField = ChoiceBoxes.forTypes(
        resourceBundle = ResourceBundles.valueTypes(),
        classes = ValueTypes.all(),
    )
    private val color = Labels.label(ChangeColumn::class,"color","Color")
    private val colorField = HashedColorPicker(nameField.valueProperty())


    init {
        bindCss("new-value")
        
        columnWeights(1.0, 3.0)

        add(name, 0, 0)
        add(nameField, 1, 0)
        add(short, 0, 1)
        add(shortField, 1, 1)
        add(type, 0, 2)
        add(typeField, 1, 2, HPos.LEFT)
        add(color, 0, 3)
        add(colorField, 1, 3, HPos.LEFT)
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.invalidInputs(nameField, typeField)
    }

    override fun result(): NewValue? {
        return NewValue(Name(nameField.text, shortField.text), typeField.selectionModel.selectedItem, colorField.value)
    }

    data class NewValue(
        val name: Name,
        val type: KClass<out Any>,
        val color: Color
    )

    companion object {
        fun open(): NewValue? {
            return DialogWrapper.open { NewValue() }
        }
    }
}