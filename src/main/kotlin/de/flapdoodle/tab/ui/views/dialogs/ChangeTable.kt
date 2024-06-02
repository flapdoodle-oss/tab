package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import javafx.beans.value.ObservableValue

class ChangeTable<K: Comparable<K>>(
    private val node: Node.Table<K>
) : DialogContent<ModelChange>() {
    private val name = Labels.label(ChangeTable::class, "name", "Name")
    private val nameField = ValidatingTextField(converter = Converters.validatingConverter(String::class)
        .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })

    init {
        bindCss("change-table")

        columnWeights(1.0, 3.0)
        add(name, 0, 0)
        add(nameField, 1, 0)

        nameField.set(node.name.long)
    }


    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.invalidInputs(nameField)
    }

    override fun result(): ModelChange? {
        return if (node.name.long != nameField.text)
            ModelChange.ChangeTableProperties(node.id, Name(nameField.text))
        else
            null
    }
}