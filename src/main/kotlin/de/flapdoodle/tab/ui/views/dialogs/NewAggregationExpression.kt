package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.css.bindCss
import de.flapdoodle.kfx.dialogs.Dialogs
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.dialogs.AbstractDialogContent
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import de.flapdoodle.tab.ui.views.common.HashedColorPicker
import javafx.beans.value.ObservableValue
import javafx.scene.paint.Color

class NewAggregationExpression : AbstractDialogContent<NewAggregationExpression.NamedExpression>() {

    private val name = Labels.label(NewAggregationExpression::class,"name","Name")
    private val nameField = ValidatingTextField(
        Converters.validatingConverter(String::class)
            .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val short = Labels.label(NewAggregationExpression::class,"shortName","Short")
    private val shortField = ValidatingTextField(converter = Converters.validatingConverter(String::class))
    private val expression = Labels.label(NewAggregationExpression::class,"expression","Expression")
    private val expressionField = ValidatingTextField(
        Converters.validatingConverter(String::class)
            .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })

    private val color = Labels.label(ChangeColumn::class,"color","Color")
    private val colorField = HashedColorPicker(nameField.valueProperty())

    init {
        bindCss("new-aggregation-expression")
        
        columnWeights(1.0, 3.0)

        add(name, 0, 0)
        add(nameField, 1, 0)
        add(short, 0, 1)
        add(shortField, 1, 1)
        add(expression, 0, 2)
        add(expressionField, 1, 2)
        add(color, 0, 3)
        add(colorField, 1, 3)
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.validInputs(nameField, expressionField)
    }

    override fun result(): NamedExpression {
        return NamedExpression(Name(nameField.text, shortField.text), expressionField.text, colorField.value)
    }


    data class NamedExpression(
        val name: Name,
        val expression: String,
        val color: Color
    )

    companion object {
        fun open(): NamedExpression? {
            return Dialogs.open(::NewAggregationExpression)
        }
    }
}