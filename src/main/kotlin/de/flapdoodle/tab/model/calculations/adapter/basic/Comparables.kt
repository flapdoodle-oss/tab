package de.flapdoodle.tab.model.calculations.adapter.basic

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.tab.model.calculations.adapter.CommonTypeMappings
import de.flapdoodle.tab.model.calculations.adapter.Evaluables
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Objects

object Comparables {
    sealed class Compare<T : Comparable<T>>(
        val check: (Int) -> Boolean
    ) : TypedEvaluable.Arg2<T, T, Boolean> {

        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token,
            first: T?,
            second: T?
        ): Boolean {
            return when {
                first == null && second == null -> check(0)
                first != null && second == null -> check(1)
                first == null && second != null -> check(-1)
                first != null && second != null -> check(first.compareTo(second))
                else -> throw IllegalArgumentException("$token: should not happen - first: $first, second: $second")
            }
        }

        class CompareGreater<T : Comparable<T>>() : Compare<T>({ it > 0 })
        class CompareGreaterOrEqual<T : Comparable<T>>() : Compare<T>({ it >= 0 })
        class CompareLess<T : Comparable<T>>() : Compare<T>({ it < 0 })
        class CompareLessOrEqual<T : Comparable<T>>() : Compare<T>({ it <= 0 })
    }

    object Equals : Evaluables(
        of(boolean, javaAnyParameterLazy, javaAnyParameterLazy, TypedEvaluable.Arg2 { _, _, check, l, r ->
            Objects.equals(l, r)
        }),
    ) {
        override fun toString(): String {
            return Equals::class.toString()
        }
    }

    object Less : Evaluables(
        listOf(
            of(boolean, bigDecimalNullable, bigDecimalNullable, Compare.CompareLess<BigDecimal>()),
            of(boolean, bigIntNullable, bigIntNullable, Compare.CompareLess<BigInteger>()),
            of(boolean, javaDoubleNullable, javaDoubleNullable, Compare.CompareLess<Double>()),
            of(boolean, javaIntNullable, javaIntNullable, Compare.CompareLess<Int>()),
        ) + CommonTypeMappings.mappings.map {
            mapped2(boolean, Compare.CompareLess(), it)
        }
    ) {
        override fun toString(): String {
            return Less::class.toString()
        }
    }

    object LessOrEquals : Evaluables(
        listOf(
            of(boolean, bigDecimalNullable, bigDecimalNullable, Compare.CompareLessOrEqual<BigDecimal>()),
            of(boolean, bigIntNullable, bigIntNullable, Compare.CompareLessOrEqual<BigInteger>()),
            of(boolean, javaDoubleNullable, javaDoubleNullable, Compare.CompareLessOrEqual<Double>()),
            of(boolean, javaIntNullable, javaIntNullable, Compare.CompareLessOrEqual<Int>())
        ) + CommonTypeMappings.mappings.map {
            mapped2(boolean, Compare.CompareLessOrEqual(), it)
        }
    ) {
        override fun toString(): String {
            return LessOrEquals::class.toString()
        }
    }

    object Greater : Evaluables(
        listOf(
            of(boolean, bigDecimalNullable, bigDecimalNullable, Compare.CompareGreater<BigDecimal>()),
            of(boolean, bigIntNullable, bigIntNullable, Compare.CompareGreater<BigInteger>()),
            of(boolean, javaDoubleNullable, javaDoubleNullable, Compare.CompareGreater<Double>()),
            of(boolean, javaIntNullable, javaIntNullable, Compare.CompareGreater<Int>())
        ) + CommonTypeMappings.mappings.map {
            mapped2(boolean, Compare.CompareGreater(), it)
        }
    ) {
        override fun toString(): String {
            return Greater::class.toString()
        }
    }

    object GreaterOrEquals : Evaluables(
        listOf(
            of(boolean, bigDecimalNullable, bigDecimalNullable, Compare.CompareGreaterOrEqual<BigDecimal>()),
            of(boolean, bigIntNullable, bigIntNullable, Compare.CompareGreaterOrEqual<BigInteger>()),
            of(boolean, javaDoubleNullable, javaDoubleNullable, Compare.CompareGreaterOrEqual<Double>()),
            of(boolean, javaIntNullable, javaIntNullable, Compare.CompareGreaterOrEqual<Int>())
        ) + CommonTypeMappings.mappings.map {
            mapped2(boolean, Compare.CompareGreaterOrEqual(), it)
        }
    ) {
        override fun toString(): String {
            return GreaterOrEquals::class.toString()
        }
    }
}