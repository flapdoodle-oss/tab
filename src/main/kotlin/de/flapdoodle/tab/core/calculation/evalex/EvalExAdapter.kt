package de.flapdoodle.tab.core.calculation.evalex

import com.ezylang.evalex.Expression
import com.ezylang.evalex.config.ExpressionConfiguration
import com.ezylang.evalex.data.MapBasedDataAccessor
import com.ezylang.evalex.operators.OperatorIfc
import de.flapdoodle.tab.core.values.ColumnType


object EvalExAdapter {
    val configuration = ExpressionConfiguration.builder()
//        .allowOverwriteConstants(true)
//        .arraysAllowed(true)
//        .dataAccessorSupplier { MapBasedDataAccessor() }
        .decimalPlacesRounding(ExpressionConfiguration.DECIMAL_PLACES_ROUNDING_UNLIMITED)
        .defaultConstants(ExpressionConfiguration.StandardConstants)
//        .functionDictionary(ExpressionConfiguration.StandardFunctionsDictionary)
        .implicitMultiplicationAllowed(true)
        .mathContext(ExpressionConfiguration.DEFAULT_MATH_CONTEXT)
//        .operatorDictionary(ExpressionConfiguration.StandardOperatorsDictionary)
        .powerOfPrecedence(OperatorIfc.OPERATOR_PRECEDENCE_POWER)
        .stripTrailingZeros(true)
//        .structuresAllowed(true)
        .build()

    fun <C: Any> parse(columnType: ColumnType<C>, expression: String): EvalExCalculator<C> {
        val parsed = Expression(expression,  configuration)
        parsed.validate()

        return EvalExCalculator(columnType)
    }
}