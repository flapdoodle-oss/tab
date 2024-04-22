package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.config.IndexTypes
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData
import kotlin.reflect.KClass

class NewCalculationDialog : Dialog<de.flapdoodle.tab.model.Node.Calculated<out Comparable<*>>>() {

    private val name = Label("Name")
    private val nameField = TextField()
    private val type = Label("Type")
    private val typeField = ChoiceBox<KClass<out Comparable<*>>>().apply {
        items.addAll(IndexTypes.all())
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
                val type = typeField.selectionModel.selectedItem
                nodeOf(nameField.text, type as KClass<out Comparable<Any>>)
            } else null
        }
    }

    private fun <K: Comparable<K>> nodeOf(name: String?, type: KClass<K>?): de.flapdoodle.tab.model.Node.Calculated<K>? {
        if (name!=null && type!=null) {
            return de.flapdoodle.tab.model.Node.Calculated(name, type)
        }
        return null
    }


    companion object {
        fun open(): de.flapdoodle.tab.model.Node.Calculated<out Comparable<*>>? {
            val dialog = NewCalculationDialog()
            return dialog.showAndWait().orElse(null)
        }
    }
}