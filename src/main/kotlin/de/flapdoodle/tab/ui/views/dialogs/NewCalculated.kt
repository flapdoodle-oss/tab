package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ChoiceBoxes
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.config.IndexTypes
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Title
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.dialogs.DialogContent
import de.flapdoodle.tab.ui.dialogs.DialogWrapper
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import de.flapdoodle.tab.ui.resources.ResourceBundles
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.scene.control.TextArea

class NewCalculated : DialogContent<Node.Calculated<out Comparable<*>>>() {

    private val name = Labels.label(NewCalculated::class,"name","Name")
    private val nameField = ValidatingTextField(
        Converters.validatingConverter(String::class)
        .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val short = Labels.label(NewValues::class,"shortName","Short")
    private val shortField = ValidatingTextField(converter = Converters.validatingConverter(String::class))
    private val description = Labels.label(NewValues::class,"description","Description")
    private val descriptionField = TextArea()
    private val type = Labels.label(NewCalculated::class,"type","IndexType")
    private val typeField = ChoiceBoxes.forTypes(
        ResourceBundles.indexTypes(),
        IndexTypes.all()
    )


    init {
        bindCss("new-calculation")

        columnWeights(1.0, 3.0)

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

    override fun result(): Node.Calculated<out Comparable<*>>? {
        val type = TypeInfo.of(typeField.selectionModel.selectedItem.javaObjectType)
        return nodeOf(Title(nameField.text, shortField.text, descriptionField.text), type as TypeInfo<out Comparable<Any>>)
    }

    private fun <K: Comparable<K>> nodeOf(name: Title, type: TypeInfo<K>?): Node.Calculated<K>? {
        if (type!=null) {
            return Node.Calculated(name, type)
        }
        return null
    }


    companion object {
        fun open(): Node.Calculated<out Comparable<*>>? {
            return DialogWrapper.open { NewCalculated() }
        }
    }
}