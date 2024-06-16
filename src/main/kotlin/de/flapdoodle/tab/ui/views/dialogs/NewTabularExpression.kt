package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ChoiceBoxes
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import de.flapdoodle.tab.ui.resources.ResourceBundles
import javafx.beans.value.ObservableValue

class NewTabularExpression : DialogContent<NewTabularExpression.NamedExpression>() {

    private val name = Labels.label(NewTabularExpression::class,"name","Name")
    private val nameField = ValidatingTextField(
        Converters.validatingConverter(String::class)
            .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val short = Labels.label(NewTabularExpression::class,"shortName","Short")
    private val shortField = ValidatingTextField(converter = Converters.validatingConverter(String::class))
    private val expression = Labels.label(NewTabularExpression::class,"expression","Expression")
    private val expressionField = ValidatingTextField(
        Converters.validatingConverter(String::class)
            .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val interpolation = Labels.label(NewColumn::class, "interpolation", "Interpolation")
    private val interpolationField = ChoiceBoxes.forEnums(
        resourceBundle = ResourceBundles.enumTypes(),
        enumType = InterpolationType::class,
        default = InterpolationType.Linear
    )

    init {
        bindCss("new-tabular-expression")
        
        columnWeights(1.0, 3.0)

        add(name, 0, 0)
        add(nameField, 1, 0)
        add(short, 0, 1)
        add(shortField, 1, 1)
        add(expression, 0, 2)
        add(expressionField, 1, 2)
        add(interpolation, 0, 3)
        add(interpolationField, 1, 3)
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.invalidInputs(nameField, expressionField)
    }

    override fun result(): NamedExpression {
        return NamedExpression(Name(nameField.text, shortField.text), expressionField.text, interpolationField.value)
    }


    data class NamedExpression(
        val name: Name,
        val expression: String,
        val interpolationType: InterpolationType
    )

    companion object {
        fun open(): NamedExpression? {
            return DialogWrapper.open { NewTabularExpression() }
        }
    }
}