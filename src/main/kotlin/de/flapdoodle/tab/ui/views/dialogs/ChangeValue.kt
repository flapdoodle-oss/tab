package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.colors.HashedColors
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import javafx.beans.value.ObservableValue
import javafx.scene.control.ColorPicker

class ChangeValue<T: Any>(
    private val nodeId: Id<out de.flapdoodle.tab.model.Node.Constants>,
    private val value: SingleValue<T>
) : DialogContent<Change.Constants.ValueProperties>() {

    private val name = Labels.label(ChangeValue::class,"name","Name")
    private val nameField = ValidatingTextField(Converters.validatingConverter(String::class)
        .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val short = Labels.label(NewValues::class,"shortName","Short")
    private val shortField = ValidatingTextField(converter = Converters.validatingConverter(String::class))
    private val color = Labels.label(ChangeColumn::class,"color","Color")
    private val colorField = ColorPicker().apply {
        customColors.addAll(HashedColors.colors())
    }


    init {
        bindCss("change-value")
        
        columnWeights(1.0, 3.0)

        add(name, 0, 0)
        add(nameField, 1, 0)
        add(short, 0, 1)
        add(shortField, 1, 1)
        add(color, 0, 2)
        add(colorField, 1, 2)

        nameField.set(value.name.long)
        shortField.set(value.name.short)
        colorField.value=value.color
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.invalidInputs(nameField)
    }

    override fun result(): Change.Constants.ValueProperties {
        return Change.Constants.ValueProperties(nodeId, value.id, Name(nameField.text, shortField.text), colorField.value)
    }

    companion object {

        fun openWith(nodeId: Id<out de.flapdoodle.tab.model.Node.Constants>, value: SingleValue<out Any>): Change.Constants.ValueProperties? {
            return DialogWrapper.open { ChangeValue(nodeId, value) }
        }
    }
}