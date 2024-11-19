package de.flapdoodle.tab.model.calculations.adapter.basic

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.tab.model.calculations.adapter.CommonTypeMappings
import de.flapdoodle.tab.model.calculations.adapter.Evaluables
import de.flapdoodle.tab.model.calculations.adapter.TypeMapping

object Conditional : Evaluables(
    listOf(
        of(javaDouble, javaBooleanParameter, javaDoubleNullable, javaDoubleNullable, Condition()),
        of(javaInt, javaBooleanParameter, javaIntNullable, javaIntNullable, Condition()),
        of(bigInt, javaBooleanParameter, bigIntNullable, bigIntNullable, Condition()),
        of(bigDecimal, javaBooleanParameter, bigDecimalNullable, bigDecimalNullable, Condition()),
    ) +
    CommonTypeMappings.mappings.map {
        Helper.condition(it)
    } +
    listOf(
        of(javaAny, javaBooleanParameter, javaAnyParameterLazy, javaAnyParameterLazy, Condition())
    )
) {

    class Condition<T> : TypedEvaluable.Arg3<Boolean, T, T, T> {
        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token,
            check: Boolean,
            left: T?,
            right: T?
        ): T? {
            //println("check: $check -> $left(${left?.javaClass}) | $right(${right?.javaClass})")
            return if (check) left else right
        }

    }

    object Helper {
        fun <T: Comparable<T>> condition(mapping: TypeMapping<out Any, out Any, T>): TypedEvaluable<T> {
            return mapped3(mapping.destination, Condition(), mapping)
        }

        fun <SOURCE_A, SOURCE_B, MAPPED: Comparable<MAPPED>, T> mapped3(type: Class<T>, delegate: TypedEvaluable.Arg3<Boolean, MAPPED, MAPPED, T>, mapping: TypeMapping<SOURCE_A,SOURCE_B,MAPPED>): TypedEvaluable<T> {
            return of(
                type,
                javaBooleanParameter,
                mapping.left,
                mapping.right,
                map3<Boolean, Boolean, SOURCE_A, MAPPED, SOURCE_B, MAPPED, T>(
                    delegate,
                    { it },
                    mapping.mapLeft,
                    mapping.mapRight
                )
            )
        }
    }
}