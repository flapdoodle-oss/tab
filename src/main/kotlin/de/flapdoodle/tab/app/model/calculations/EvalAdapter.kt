package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.Expression
import de.flapdoodle.eval.core.ExpressionFactory
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluableMap
import de.flapdoodle.eval.core.evaluables.TypedEvaluables
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.eval.example.Defaults
import de.flapdoodle.eval.example.Defaults.*
import de.flapdoodle.eval.example.Value
import de.flapdoodle.eval.example.Value.NumberValue
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

data class EvalAdapter(
    private val formula: String,
    private val expression: Expression = expressionFactory.parse(formula),
    private val variablesWithHash: List<Pair<Variable, Int>> = expression.usedVariablesWithHash()
        .map { Variable(it.key) to it.value }
): Formula {

    private val variables = variablesWithHash.map { it.first }.toCollection(linkedSetOf())

    override fun expression() = formula
    override fun variables(): Set<Variable> = variables

    override fun evaluate(values: Map<Variable, Any?>): Any? {
        var resolver = VariableResolver.empty()
        values.forEach { variable, value ->
            resolver = if (value != null) {
                resolver.with(variable.name, mapToValue(value))
            } else {
                resolver.with(variable.name, Value.ofNull())
            }
        }
        return mapBack(expression.evaluate(resolver))
    }

    private fun mapToValue(value: Any): Value<out Any> {
        return when (value) {
            is Int -> Value.of(value.toBigDecimal())
            is BigDecimal -> Value.of(value)
            is Double -> Value.of(value)
            else -> throw IllegalArgumentException("not implemented: $value (${value::class})")
        }
    }

    private fun mapBack(value: Any?): Any? {
        if (value is Value<out Any>) return value.wrapped()
        return value
    }

    override fun change(newFormula: String): EvalAdapter {
        return if (newFormula != formula) {
            val changedExpression = expressionFactory.parse(newFormula)
            val byId = variablesWithHash.associateBy { it.second }
            val byName = variablesWithHash.associateBy { it.first.name }
            val changedVariables = changedExpression.usedVariablesWithHash().map {
                val old = byId[it.value]?.first
                if (old!=null) {
                    if (it.key != old.name) {
                        old.copy(name = it.key) to it.value
                    } else {
                        old to it.value
                    }
                } else {
                    // different hash
                    val sameName = byName[it.key]
                    if (sameName!=null) {
                        sameName.first to it.value
                    } else {
                        Variable(it.key) to it.value
                    }
                }
            }
            copy(
                formula = newFormula,
                expression = changedExpression,
                variablesWithHash = changedVariables
            )
        } else {
            this
        }
    }

    companion object {
        private val expressionFactory = Defaults.expressionFactory()
        
        private val expressionFactoryC = ExpressionFactory.builder()
            .constants(constants())
            .evaluatables(TypedEvaluableMap.builder()
                .putMap("sum", Plus())
                .build())
            .arrayAccess(arrayAccess())
            .propertyAccess(propertyAccess())
            .numberAsValue { value: String?, mathContext: MathContext? -> numFromString(value!!, mathContext) }
            .stringAsValue { s: String? -> valueFromString(s) }
            .operatorMap(operatorMap())
            .exceptionMapper(exceptionMapper())
            .build()

        fun numFromString(value: String, mathContext: MathContext?): Any {
            if (value.startsWith("0x") || value.startsWith("0X")) {
                val hexToInteger = BigInteger(value.substring(2), 16)
                //return Value.of(BigDecimal(hexToInteger, mathContext))
            } else {
                //return Value.of(BigDecimal(value, mathContext))
            }

            return Integer.valueOf(value)
        }


        class Plus : TypedEvaluables.Wrapper(
            TypedEvaluables.builder()
                .addList(
                    TypedEvaluable.of(
                        java.lang.Integer::class.java,
                        java.lang.Integer::class.java,
                        java.lang.Integer::class.java, PlusInt()
                    )
                )
                .build()) {
        }

        class PlusInt : TypedEvaluable.Arg2<Integer, Integer, Integer> {
            override fun evaluate(
                variableResolver: VariableResolver?,
                evaluationContext: EvaluationContext?,
                token: Token?,
                first: Integer,
                second: Integer
            ): Integer? {
                return Integer.valueOf(first.toInt() + second.toInt()) as Integer
            }

        }
    }
}