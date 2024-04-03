package de.flapdoodle.tab.app.ui.views.dialogs

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.app.model.data.Column
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.ButtonType
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextField
import java.time.LocalDate
import kotlin.reflect.KClass

class NewColumnDialog<K: Comparable<K>>(
    val indexType: KClass<in K>
) : Dialog<Column<K, out Any>>() {

    private val name = Label("Name")
    private val nameField = TextField()
    private val type = Label("Type")
    private val typeField = ChoiceBox<KClass<out Any>>().apply {
        items.addAll(Int::class, Double::class, String::class, LocalDate::class)
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
                Column(nameField.text, indexType, typeField.selectionModel.selectedItem)
            } else null
        }
    }

    companion object {
        fun <K: Comparable<K>> open(indexType: KClass<in K>): Column<K, out Any>? {
            val dialog = NewColumnDialog(indexType)
            return dialog.showAndWait().orElse(null)
        }
    }
}