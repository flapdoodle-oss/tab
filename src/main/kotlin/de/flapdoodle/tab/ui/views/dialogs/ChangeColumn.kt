package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ChoiceBoxes
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.config.ValueTypes
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node.Table
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import de.flapdoodle.tab.ui.resources.ResourceBundles
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos

class ChangeColumn<K : Comparable<K>>(
    private val nodeId: Id<out Table<*>>,
    private val column: Column<K, out Any>
) : DialogContent<ModelChange.ChangeColumnProperties<K>>() {

    private val name = Labels.name(ChangeColumn::class)
    private val nameField = ValidatingTextField(
        Converters.validatingConverter(String::class)
            .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val short = Labels.label(ChangeColumn::class,"shortName","Short")
    private val shortField = ValidatingTextField(converter = Converters.validatingConverter(String::class))

    private val interpolation = Labels.label(ChangeColumn::class, "interpolation", "Interpolation")
    private val interpolationField = ChoiceBoxes.forEnums(
        resourceBundle = ResourceBundles.enumTypes(),
        enumType = InterpolationType::class,
        default = column.interpolationType
    )

    init {
        println("try to change column: $column")

        bindCss("change-column")

        columnWeights(1.0, 3.0)

        add(name, 0, 0)
        add(nameField, 1, 0)
        add(short, 0, 1)
        add(shortField, 1, 1)
        add(interpolation, 0, 2)
        add(interpolationField, 1, 2, HPos.LEFT)

        nameField.set(column.name.long)
        shortField.set(column.name.short)
//        interpolationField.value = column.interpolationType
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.invalidInputs(nameField, interpolationField)
    }

    override fun result(): ModelChange.ChangeColumnProperties<K>? {
        val newName = Name(nameField.text, shortField.text)
        return if (column.name!= newName || column.interpolationType != interpolationField.selectionModel.selectedItem) {
            ModelChange.ChangeColumnProperties(nodeId, column.id, newName, interpolationField.selectionModel.selectedItem)
        } else null
    }

    companion object {
        fun <K : Comparable<K>> open(nodeId: Id<out Table<*>>, column: Column<K, out Any>): ModelChange.ChangeColumnProperties<K>? {
            return DialogWrapper.open { ChangeColumn(nodeId, column) }
        }
    }
}