package de.flapdoodle.tab.app.ui.views.dialogs

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.app.model.Node
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextField

class NewValuesDialog : Dialog<Node.Constants>() {

    private val name = Label("Name")
    private val nameField = TextField()

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

        setResultConverter { dialogButton: ButtonType? ->
            if (dialogButton?.buttonData == ButtonData.OK_DONE) {
                Node.Constants(nameField.text)
            } else null
        }
    }


    companion object {
        fun open(): Node.Constants? {
            val dialog = NewValuesDialog()
            return dialog.showAndWait().orElse(null)
        }
    }
}