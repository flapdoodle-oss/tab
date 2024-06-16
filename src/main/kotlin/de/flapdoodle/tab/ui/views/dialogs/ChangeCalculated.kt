package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Title
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import javafx.beans.value.ObservableValue
import javafx.scene.control.TextArea

class ChangeCalculated<K: Comparable<K>>(
    private val node: Node.Calculated<K>
) : DialogContent<Change.Calculation.Properties>() {
    private val name = Labels.label(ChangeCalculated::class, "name", "Name")
    private val nameField = ValidatingTextField(converter = Converters.validatingConverter(String::class)
        .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val short = Labels.label(NewValues::class,"shortName","Short")
    private val shortField = ValidatingTextField(converter = Converters.validatingConverter(String::class))
    private val description = Labels.label(NewValues::class,"description","Description")
    private val descriptionField = TextArea()

    init {
        bindCss("change-calculation")

        columnWeights(0.0, 1.0)
        add(name, 0, 0)
        add(nameField, 1, 0)
        add(short, 0, 1)
        add(shortField, 1, 1)
        add(description, 0, 2)
        add(descriptionField, 1, 2)

        nameField.set(node.name.long)
        shortField.set(node.name.short)
        descriptionField.text = node.name.description
    }


    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.invalidInputs(nameField)
    }

    override fun result(): Change.Calculation.Properties? {
        val newName = Title(nameField.text, shortField.text, descriptionField.text)
        return if (node.name != newName)
            Change.Calculation.Properties(node.id, newName)
        else
            null
    }
}