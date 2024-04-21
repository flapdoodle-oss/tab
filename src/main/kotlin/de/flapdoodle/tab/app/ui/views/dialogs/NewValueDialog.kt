package de.flapdoodle.tab.app.ui.views.dialogs

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData
import kotlin.reflect.KClass

class NewValueDialog : Dialog<NewValueDialog.NewValue>() {

    private val name = Label("Name")
    private val nameField = TextField()
    private val type = Label("Type")
    private val typeField = ChoiceBox<KClass<out Any>>().apply {
        items.addAll(Int::class, Double::class, String::class)
        value = Int::class
    }

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
            WeightGridPane.setPosition(typeField, 1, 1)

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