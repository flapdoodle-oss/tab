package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.i18n.I18N
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.Messages
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextField

class NewValuesDialog : Dialog<de.flapdoodle.tab.model.Node.Constants>() {

    private val name = Labels.translated(NewValuesDialog::class,"name","Name")
    private val nameField = ValidatingTextField(Converters.validatingConverter(String::class))

    init {
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        dialogPane.content = WeightGridPane().apply {
            horizontalSpaceProperty().value = 10.0
            verticalSpaceProperty().value = 10.0

            setColumnWeight(0, 1.0)
            setColumnWeight(1, 3.0)

            WeightGridPane.setPosition(name, 0, 0)
            WeightGridPane.setPosition(nameField, 1, 0)

            children.addAll(name, nameField)
        }
        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.disableProperty().bind(ValidatingField.invalidInputs(nameField))

        setResultConverter { dialogButton: ButtonType? ->
            if (dialogButton?.buttonData == ButtonData.OK_DONE) {
                require(nameField.text != null && !nameField.text.isBlank()) {"name not set"}
                de.flapdoodle.tab.model.Node.Constants(nameField.text)
            } else null
        }
    }


    companion object {
        fun open(): de.flapdoodle.tab.model.Node.Constants? {
            val dialog = NewValuesDialog()
            return dialog.showAndWait().orElse(null)
        }
    }
}