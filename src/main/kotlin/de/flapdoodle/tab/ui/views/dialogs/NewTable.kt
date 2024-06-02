package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ChoiceBoxes
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.config.IndexTypes
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import de.flapdoodle.tab.ui.resources.ResourceBundles
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.scene.control.TextArea

class NewTable : DialogContent<Node.Table<out Comparable<*>>>() {

    private val name = Labels.name(NewTable::class)
    private val nameField = ValidatingTextField(Converters.validatingConverter(String::class)
        .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val short = Labels.label(NewValues::class,"shortName","Short")
    private val shortField = ValidatingTextField(converter = Converters.validatingConverter(String::class))
    private val description = Labels.label(NewValues::class,"description","Description")
    private val descriptionField = TextArea()
    private val type = Labels.label(NewTable::class,"type","IndexType")
    private val typeField = ChoiceBoxes.forTypes(
        ResourceBundles.indexTypes(),
        IndexTypes.all()
    )
    
    init {
        bindCss("new-table")

        columnWeights(0.0, 1.0)

        add(name, 0, 0)
        add(nameField, 1, 0)
        add(short, 0, 1)
        add(shortField, 1, 1)
        add(description, 0, 2)
        add(descriptionField, 1, 2)
        add(type, 0, 3)
        add(typeField, 1, 3, HPos.LEFT)
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.invalidInputs(nameField, typeField)
    }

    override fun result(): Node.Table<out Comparable<*>>? {
        val type = TypeInfo.of(typeField.selectionModel.selectedItem.javaObjectType)
        return nodeOf(Name(nameField.text, shortField.text, descriptionField.text), type as TypeInfo<out Comparable<Any>>)
    }

    private fun <K: Comparable<K>> nodeOf(name: Name, indexType: TypeInfo<K>?): Node.Table<K>? {
        if (indexType!=null) {
            return Node.Table(
                name = name,
                indexType = indexType
            )
        }
        return null
    }


    companion object {
        fun open(): de.flapdoodle.tab.model.Node.Table<out Comparable<*>>? {
            return DialogWrapper.open { NewTable() }
        }
    }
}