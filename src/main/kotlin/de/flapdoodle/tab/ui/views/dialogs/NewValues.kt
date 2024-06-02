package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import javafx.beans.value.ObservableValue
import javafx.scene.control.TextArea

class NewValues() : DialogContent<Node.Constants>() {

    private val name = Labels.label(NewValues::class,"name","Name")
    private val nameField = ValidatingTextField(converter = Converters.validatingConverter(String::class)
        .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val short = Labels.label(NewValues::class,"shortName","Short")
    private val shortField = ValidatingTextField(converter = Converters.validatingConverter(String::class))
    private val description = Labels.label(NewValues::class,"description","Description")
    private val descriptionField = TextArea()

    init {
        bindCss("new-values")

        columnWeights(0.0, 1.0)
        add(name, 0, 0)
        add(nameField, 1, 0)
        add(short, 0, 1)
        add(shortField, 1, 1)
        add(description, 0, 2)
        add(descriptionField, 1, 2)
    }


    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.invalidInputs(nameField)
    }

    override fun result(): Node.Constants {
        require(nameField.text != null && !nameField.text.isBlank()) {"name not set"}
        return Node.Constants(Name(
            nameField.text,
            shortField.text,
            descriptionField.text
        ))
    }

    companion object {
        fun open(): Node.Constants? {
            return DialogWrapper.open { NewValues() }
        }
    }
}