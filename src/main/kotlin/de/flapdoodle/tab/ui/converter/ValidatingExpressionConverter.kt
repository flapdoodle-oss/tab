package de.flapdoodle.tab.ui.converter

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.eval.core.exceptions.ParseException
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import de.flapdoodle.tab.model.calculations.adapter.Eval

class ValidatingExpressionConverter : ValidatingConverter<Expression> {
    override fun fromString(value: String?): ValueOrError<Expression> {
        return if (value!=null) {
            try {
                ValueOrError.Value(Eval.parse(value))
            } catch (ex: ParseException) {
                ValueOrError.error(ex)
            }
        } else {
            ValueOrError.error(IllegalArgumentException("no expression"))
        }
    }

    override fun toString(value: Expression): String {
        return value.source()
    }
}