package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ChoiceBoxes
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.config.IndexTypes
import de.flapdoodle.tab.ui.resources.ResourceBundles
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData

class NewTableDialog : Dialog<de.flapdoodle.tab.model.Node.Table<out Comparable<*>>>() {

    private val name = Label("Name")
    private val nameField = TextField()
    private val type = Label("Type")
    private val typeField = ChoiceBoxes.forTypes(
        ResourceBundles.indexTypes(),
        IndexTypes.all(),
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
            WeightGridPane.setPosition(typeField, 1, 1)

            children.addAll(name, nameField, type, typeField)
        }

        setResultConverter { dialogButton: ButtonType? ->
            if (dialogButton?.buttonData == ButtonData.OK_DONE) {
                val type = TypeInfo.of(typeField.selectionModel.selectedItem.javaObjectType)
                nodeOf(nameField.text, type as TypeInfo<out Comparable<Any>>)
            } else null
        }
    }

    private fun <K: Comparable<K>> nodeOf(name: String?, indexType: TypeInfo<K>?): de.flapdoodle.tab.model.Node.Table<K>? {
        if (name!=null && indexType!=null) {
            return de.flapdoodle.tab.model.Node.Table(
                name = name,
                indexType = indexType
            )
        }
        return null
    }


    companion object {
        fun open(): de.flapdoodle.tab.model.Node.Table<out Comparable<*>>? {
            val dialog = NewTableDialog()
            return dialog.showAndWait().orElse(null)
        }
    }
}