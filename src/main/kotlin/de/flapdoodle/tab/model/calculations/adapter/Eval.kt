package de.flapdoodle.tab.model.calculations.adapter

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.eval.core.ExpressionFactory
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.*
import de.flapdoodle.tab.model.calculations.Variable
import de.flapdoodle.tab.model.calculations.adapter.arithmetic.*
import de.flapdoodle.tab.model.calculations.adapter.basic.Comparables
import de.flapdoodle.tab.model.calculations.adapter.basic.Conditional
import de.flapdoodle.tab.model.calculations.adapter.basic.Round
import de.flapdoodle.tab.model.calculations.adapter.booleans.Combine
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

object Eval {

    private val constants = VariableResolver.empty().withVariables(
        mapOf(
            "true" to Evaluated.value(true),
            "false" to Evaluated.value(false),
            "PI" to Evaluated.value(BigDecimal("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679")),
            "E" to Evaluated.value(BigDecimal("2.71828182845904523536028747135266249775724709369995957496696762772407663"))
        ))

    private val operatorMap = OperatorMap.builder()
        .putPrefix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "sum"))
        .putPrefix("-", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "minus"))
        .putPrefix("!", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "not"))

        .putInfix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "sum"))
        .putInfix("-", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "minus"))
        .putInfix("*", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_MULTIPLICATIVE, "multiply"))
        .putInfix("/", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_MULTIPLICATIVE, "divide"))
        .putInfix("^", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_POWER, false, "power"))
        .putInfix("%", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_MULTIPLICATIVE, "modulo")) // booleans
        .putInfix("=", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "equal"))
        .putInfix("==", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "equal"))
        .putInfix("===", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "same"))
        .putInfix("!=", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "notequal"))
        .putInfix("<>", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "notequal"))
        .putInfix(">", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_COMPARISON, "greater"))
        .putInfix(">=", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_COMPARISON, "greaterOrEqual"))
        .putInfix("<", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_COMPARISON, "less"))
        .putInfix("<=", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_COMPARISON, "lessOrEqual"))
        .putInfix("&&", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_AND, "and"))
        .putInfix("||", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_OR, "or"))
        .putPrefix("!", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, "not"))
        .build();

    private val expressionFactory = ExpressionFactory.builder()
        .constants(constants)
        .arrayAccess(ArrayAccess)
        .associateAccess(ArrayAccess)
        .propertyAccess(PropertyAccess)
        .numberAsValue { value: String, mathContext: MathContext? -> numFromString(value, mathContext) }
        .stringAsValue { it }
        .operatorMap(operatorMap)
        .exceptionMapper(ExceptionMapper)
        .evaluatables(TypedEvaluableMap.builder()
            .putMap("sum", Plus)
            .putMap("multiply", Multiply)
            .putMap("minus", Minus)
            .putMap("divide", Divide)
            .putMap("avg", Avg)
            .putMap("round", Round.Numbers)
            .putMap("if", Conditional)
            .putMap("equal", Comparables.Equals)
            .putMap("less", Comparables.Less)
            .putMap("lessOrEqual", Comparables.LessOrEquals)
            .putMap("greater", Comparables.Greater)
            .putMap("greaterOrEqual", Comparables.GreaterOrEquals)
            .putMap("and", Combine.And)
            .putMap("or", Combine.Or)
            .putMap("not", Combine.Not)
            .build())
        .build()

    private fun numFromString(value: String, mathContext: MathContext?): Any {
        return if (value.startsWith("0x") || value.startsWith("0X")) {
            BigInteger(value.substring(2), 16)
        } else {
            val ret = BigDecimal(value, mathContext)
            if (isIntegerValue(ret)) {
                ret.toBigIntegerExact()
            } else
                ret
        }
    }

    private fun isIntegerValue(bd: BigDecimal): Boolean {
        return bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0
    }

    fun parse(formula: String): Expression {
        return expressionFactory.parse(formula)
    }

    fun variablesWithHash(expression: Expression): List<Pair<Variable, Int>> {
        return expression.usedVariablesWithHash()
            .map { Variable(it.key) to it.value }
    }
}