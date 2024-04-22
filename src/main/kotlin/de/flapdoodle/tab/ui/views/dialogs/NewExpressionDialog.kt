package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextField

class NewExpressionDialog : Dialog<NewExpressionDialog.NewExpression>() {

    private val name = Label("Name")
    private val nameField = TextField()
    private val expression = Label("Expression")
    private val expressionField = TextField()

    init {
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        dialogPane.content = WeightGridPane().apply {
            horizontalSpaceProperty().value = 10.0
            verticalSpaceProperty().value = 10.0

            setColumnWeight(0, 1.0)
            setColumnWeight(1, 3.0)

            WeightGridPane.setPosition(name, 0, 0)
            WeightGridPane.setPosition(nameField, 1, 0)
            WeightGridPane.setPosition(expression, 0, 1)
            WeightGridPane.setPosition(expressionField, 1, 1)

            children.addAll(name, nameField, expression, expressionField)
        }

        setResultConverter { dialogButton: ButtonType? ->
            if (dialogButton?.buttonData == ButtonData.OK_DONE) {
                NewExpression(nameField.text, expressionField.text)
            } else null
        }
//        setResultConverter(Callback<ButtonType, String?> { dialogButton: ButtonType? ->
//            val data = dialogButton?.buttonData
//            if (data == ButtonData.OK_DONE) textField.getText() else null
//        })
    }


    data class NewExpression(
        val name: String,
        val expression: String
    )

    companion object {
        fun open(): NewExpression? {
            val dialog = NewExpressionDialog()
            return dialog.showAndWait().orElse(null)
        }
    }
}