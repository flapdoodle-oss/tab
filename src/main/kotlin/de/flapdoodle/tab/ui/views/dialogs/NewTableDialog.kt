package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ChoiceBoxes
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.config.IndexTypes
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import de.flapdoodle.tab.ui.resources.ResourceBundles
import javafx.geometry.HPos
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData

class NewTableDialog : Dialog<de.flapdoodle.tab.model.Node.Table<out Comparable<*>>>() {

    private val name = Labels.label(NewTableDialog::class,"name","Name")
    private val nameField = ValidatingTextField(Converters.validatingConverter(String::class)
        .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val type = Labels.label(NewTableDialog::class,"type","IndexType")
    private val typeField = ChoiceBoxes.forTypes(
        ResourceBundles.indexTypes(),
        IndexTypes.all()
    )
    
    init {
        title = Labels.text(NewTableDialog::class,"title","New Table")
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
        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.disableProperty().bind(ValidatingField.invalidInputs(nameField, typeField))

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