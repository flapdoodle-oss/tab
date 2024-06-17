package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.kfx.colors.HashedColors
import de.flapdoodle.kfx.controls.fields.ValidatingField
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.ui.Converters
import de.flapdoodle.tab.ui.converter.ValidatingExpressionConverter
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.resources.RequiredFieldNotSet
import javafx.beans.value.ObservableValue
import javafx.scene.control.ColorPicker
import javafx.scene.paint.Color

class ChangeAggregationExpression(
    oldName: Name,
    oldExpression: Expression?,
    oldColor: Color,
) : DialogContent<ChangeAggregationExpression.NamedExpression>() {

    private val name = Labels.label(ChangeAggregationExpression::class,"name","Name")
    private val nameField = ValidatingTextField(
        Converters.validatingConverter(String::class)
            .and { v -> v.mapNullable { if (it.isNullOrBlank()) throw RequiredFieldNotSet("not set") else it } })
    private val short = Labels.label(ChangeAggregationExpression::class,"shortName","Short")
    private val shortField = ValidatingTextField(converter = Converters.validatingConverter(String::class))
    private val expression = Labels.label(ChangeAggregationExpression::class,"expression","Expression")
    private val expressionField = ValidatingTextField(ValidatingExpressionConverter())
    private val color = Labels.label(ChangeColumn::class,"color","Color")
    private val colorField = ColorPicker().apply {
        customColors.addAll(HashedColors.colors())
    }

    init {
        bindCss("change-aggregation-expression")
        
        columnWeights(1.0, 3.0)

        add(name, 0, 0)
        add(nameField, 1, 0)
        add(short, 0, 1)
        add(shortField, 1, 1)
        add(expression, 0, 2)
        add(expressionField, 1, 2)
        add(color, 0, 3)
        add(colorField, 1, 3)

        nameField.set(oldName.long)
        shortField.set(oldName.short)
        expressionField.set(oldExpression)
        colorField.value=oldColor
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
        return ValidatingField.invalidInputs(nameField, expressionField)
    }

    override fun result(): NamedExpression {
        return NamedExpression(Name(nameField.text, shortField.text), requireNotNull(expressionField.get()) { "expression not set" }, colorField.value)
    }


    data class NamedExpression(
        val name: Name,
        val expression: Expression,
        val color: Color
    )

    companion object {
        fun open(name: Name, expression: Expression?,color: Color): NamedExpression? {
            return DialogWrapper.open { ChangeAggregationExpression(name, expression, color) }
        }
    }
}