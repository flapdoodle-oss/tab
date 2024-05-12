package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ChoiceBoxes
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.config.ValueTypes
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.ResourceBundles
import javafx.geometry.HPos
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData
import kotlin.reflect.KClass

class NewValueDialog : Dialog<NewValueDialog.NewValue>() {

    private val name = Labels.translated(NewValueDialog::class,"name","Name")
    private val nameField = ValidatingTextField(Converters.validatingConverter(String::class))
    private val type = Labels.translated(NewValueDialog::class,"type","Type")
    private val typeField = ChoiceBoxes.forTypes(
        ResourceBundles.valueTypes(),
        ValueTypes.all(),
        Int::class
    )

    init {
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

            children.addAll(name, nameField, type, typeField)
        }

        setResultConverter { dialogButton: ButtonType? ->
            if (dialogButton?.buttonData == ButtonData.OK_DONE) {
                NewValue(nameField.text, typeField.selectionModel.selectedItem)
            } else null
        }
    }


    data class NewValue(
        val name: String,
        val type: KClass<out Any>
    )

    companion object {
        fun open(): NewValue? {
            val dialog = NewValueDialog()
            return dialog.showAndWait().orElse(null)
        }
    }
}