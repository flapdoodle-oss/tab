package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ChoiceBoxes
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.config.ValueTypes
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import de.flapdoodle.tab.ui.resources.ResourceBundles
import javafx.geometry.HPos
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData

class NewColumnDialog<K: Comparable<K>>(
    val indexType: TypeInfo<in K>
) : Dialog<Column<K, out Any>>() {

    private val name = Labels.label(NewColumnDialog::class,"name","Name")
    private val nameField = ValidatingTextField(
        Converters.validatingConverter(String::class)
        .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val type = Labels.label(NewColumnDialog::class,"type","Type")

    private val typeField = ChoiceBoxes.forTypes(
        ResourceBundles.valueTypes(),
        ValueTypes.all()
    )

    private val interpolation = Labels.label(NewColumnDialog::class,"interpolation","Interpolation")
    private val interpolationField = ChoiceBoxes.forEnums(
        resourceBundle = ResourceBundles.enumTypes(),
        enumType = InterpolationType::class,
        default = InterpolationType.Linear
    )

    init {
        title = Labels.text(NewColumnDialog::class,"title","New Column")
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        dialogPane.content = WeightGridPane().apply {
            horizontalSpaceProperty().value = 10.0
            verticalSpaceProperty().value = 10.0

            setColumnWeight(0, 1.0)
            setColumnWeight(1, 3.0)

            WeightGridPane.setPosition(name, 0, 0)
            WeightGridPane.setPosition(nameField, 1, 0)
            WeightGridPane.setPosition(type, 0, 1)
            WeightGridPane.setPosition(typeField, 1, 1, HPos.LEFT)
            WeightGridPane.setPosition(interpolation, 0, 2)
            WeightGridPane.setPosition(interpolationField, 1, 2, HPos.LEFT)

            children.addAll(name, nameField, type, typeField, interpolation, interpolationField)
        }
        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.disableProperty().bind(ValidatingField.invalidInputs(nameField, typeField, interpolationField))

        setResultConverter { dialogButton: ButtonType? ->
            if (dialogButton?.buttonData == ButtonData.OK_DONE) {
                Column(
                    name = nameField.text,
                    indexType = indexType,
                    valueType = TypeInfo.of(typeField.selectionModel.selectedItem.javaObjectType),
                    interpolationType = interpolationField.selectionModel.selectedItem
                )
            } else null
        }
    }

    companion object {
        fun <K: Comparable<K>> open(indexType: TypeInfo<in K>): Column<K, out Any>? {
            val dialog = NewColumnDialog(indexType)
            return dialog.showAndWait().orElse(null)
        }
    }
}