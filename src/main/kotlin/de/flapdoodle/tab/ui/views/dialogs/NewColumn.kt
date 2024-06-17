package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.colors.HashedColors
import de.flapdoodle.kfx.controls.fields.ChoiceBoxes
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.config.ValueTypes
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import de.flapdoodle.tab.ui.resources.ResourceBundles
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.scene.control.ColorPicker

class NewColumn<K : Comparable<K>>(
    val indexType: TypeInfo<in K>
) : DialogContent<Column<K, out Any>>() {

    private val name = Labels.label(NewColumn::class, "name", "Name")
    private val nameField = ValidatingTextField(
        Converters.validatingConverter(String::class)
            .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val short = Labels.label(NewColumn::class,"shortName","Short")
    private val shortField = ValidatingTextField(converter = Converters.validatingConverter(String::class))
    private val type = Labels.label(NewColumn::class, "type", "Type")
    private val typeField = ChoiceBoxes.forTypes(
        ResourceBundles.valueTypes(),
        ValueTypes.all()
    )

    private var useHashedColor = true
    private val color = Labels.label(ChangeColumn::class,"color","Color")
    private val colorField = ColorPicker().apply {
        customColors.addAll(HashedColors.colors())
        onAction = EventHandler {
            useHashedColor = false
        }
    }

    private val interpolation = Labels.label(NewColumn::class, "interpolation", "Interpolation")
    private val interpolationField = ChoiceBoxes.forEnums(
        resourceBundle = ResourceBundles.enumTypes(),
        enumType = InterpolationType::class,
        default = InterpolationType.Linear
    )

    init {
        bindCss("new-column")

        columnWeights(1.0, 3.0)

        add(name, 0, 0)
        add(nameField, 1, 0)
        add(short, 0, 1)
        add(shortField, 1, 1)
        add(type, 0, 2)
        add(typeField, 1, 2, HPos.LEFT)
        add(color, 0, 3)
        add(colorField, 1, 3, HPos.LEFT)
        add(interpolation, 0, 4)
        add(interpolationField, 1, 4, HPos.LEFT)

        nameField.valueProperty().addListener { observable, oldValue, newValue ->
            if (useHashedColor) {
                colorField.value = HashedColors.hashedColor(newValue ?: "")
            }
        }
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.invalidInputs(nameField, typeField, interpolationField)
    }

    override fun result(): Column<K, out Any>? {
        return Column(
            name = Name(nameField.text, shortField.text),
            indexType = indexType,
            valueType = TypeInfo.of(typeField.selectionModel.selectedItem.javaObjectType),
            color = colorField.value,
            interpolationType = interpolationField.selectionModel.selectedItem
        )
    }

    companion object {
        fun <K : Comparable<K>> open(indexType: TypeInfo<in K>): Column<K, out Any>? {
            return DialogWrapper.open { NewColumn(indexType) }
        }
    }
}