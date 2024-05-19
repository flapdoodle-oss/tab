package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ChoiceBoxes
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.config.IndexTypes
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import de.flapdoodle.tab.ui.resources.ResourceBundles
import javafx.beans.value.ObservableValue
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData
import kotlin.reflect.KClass

class NewCalculation : DialogContent<Node.Calculated<out Comparable<*>>>() {

    private val name = Labels.label(NewCalculation::class,"name","Name")
    private val nameField = ValidatingTextField(
        Converters.validatingConverter(String::class)
        .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val type = Labels.label(NewCalculation::class,"type","IndexType")
    private val typeField = ChoiceBoxes.forTypes(
        ResourceBundles.indexTypes(),
        IndexTypes.all()
    )


    init {
        bindCss("new-calculation")

        columnWeights(1.0, 3.0)

        add(name, 0, 0)
        add(nameField, 1, 0)
        add(type, 0, 1)
        add(typeField, 1, 1)

    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.invalidInputs(nameField, typeField)
    }

    override fun result(): Node.Calculated<out Comparable<*>>? {
        val type = TypeInfo.of(typeField.selectionModel.selectedItem.javaObjectType)
        return nodeOf(nameField.text, type as TypeInfo<out Comparable<Any>>)
    }

    private fun <K: Comparable<K>> nodeOf(name: String?, type: TypeInfo<K>?): Node.Calculated<K>? {
        if (name!=null && type!=null) {
            return Node.Calculated(name, type)
        }
        return null
    }


    companion object {
        fun open(): Node.Calculated<out Comparable<*>>? {
            return DialogWrapper.open { NewCalculation() }
        }
    }
}