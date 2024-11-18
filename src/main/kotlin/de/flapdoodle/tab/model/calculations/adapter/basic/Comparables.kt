package de.flapdoodle.tab.model.calculations.adapter.basic

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.tab.model.calculations.adapter.Evaluables
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Objects
import java.util.function.Function

object Comparables {
    sealed class Compare<T: Comparable<T>>(
        val check: (Int) -> Boolean
    ): TypedEvaluable.Arg2<T, T, Boolean> {

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
                first !=null && second != null -> check(first.compareTo(second))
                else -> throw IllegalArgumentException("$token: should not happen - first: $first, second: $second")
            }
        }

        class CompareGreater<T: Comparable<T>>(): Compare<T>({it > 0})
        class CompareGreaterOrEqual<T: Comparable<T>>(): Compare<T>({it >= 0})
        class CompareLess<T: Comparable<T>>(): Compare<T>({it < 0})
        class CompareLessOrEqual<T: Comparable<T>>(): Compare<T>({it <= 0})
    }

    object Equals : Evaluables(
        of(boolean, javaAnyParameterLazy, javaAnyParameterLazy, TypedEvaluable.Arg2 { _,_,check,l,r ->
            Objects.equals(l, r)
        }),
    ) {
        override fun toString(): String {
            return Equals::class.toString()
        }
    }
    object Less : Evaluables(
        of(boolean, bigDecimalNullable, bigDecimalNullable, Compare.CompareLess<BigDecimal>()),
        of(boolean, bigIntNullable, bigIntNullable, Compare.CompareLess<BigInteger>()),
        of(boolean, javaDoubleNullable, javaDoubleNullable, Compare.CompareLess<Double>()),
        of(boolean, javaIntNullable, javaIntNullable, Compare.CompareLess<Int>()),
        of(boolean, bigIntNullable, bigDecimalNullable, map<BigInteger, BigDecimal, BigDecimal, BigDecimal, Boolean>(Compare.CompareLess(),BigInteger::toBigDecimal, unmappedBigDecimal)),
        of(boolean, bigDecimalNullable, bigIntNullable, map<BigDecimal, BigDecimal, BigInteger, BigDecimal, Boolean>(Compare.CompareLess(),unmappedBigDecimal, BigInteger::toBigDecimal)),
        of(boolean, javaDoubleNullable, bigDecimalNullable, map<Double, BigDecimal, BigDecimal, BigDecimal, Boolean>(Compare.CompareLess(),Double::toBigDecimal, unmappedBigDecimal)),
        of(boolean, bigDecimalNullable, javaDoubleNullable, map<BigDecimal, BigDecimal, Double, BigDecimal, Boolean>(Compare.CompareLess(),unmappedBigDecimal, Double::toBigDecimal)),
        of(boolean, javaIntNullable, bigDecimalNullable, map<Int, BigDecimal, BigDecimal, BigDecimal, Boolean>(Compare.CompareLess(),Int::toBigDecimal, unmappedBigDecimal)),
        of(boolean, bigDecimalNullable, javaIntNullable, map<BigDecimal, BigDecimal, Int, BigDecimal, Boolean>(Compare.CompareLess(),unmappedBigDecimal, Int::toBigDecimal)),
    ) {
        override fun toString(): String {
            return Less::class.toString()
        }
    }
    object LessOrEquals : Evaluables(
        of(boolean, bigDecimalNullable, bigDecimalNullable, Compare.CompareLessOrEqual<BigDecimal>()),
        of(boolean, bigIntNullable, bigIntNullable, Compare.CompareLessOrEqual<BigInteger>()),
        of(boolean, javaDoubleNullable, javaDoubleNullable, Compare.CompareLessOrEqual<Double>()),
        of(boolean, javaIntNullable, javaIntNullable, Compare.CompareLessOrEqual<Int>()),
        of(boolean, bigIntNullable, bigDecimalNullable, map<BigInteger, BigDecimal, BigDecimal, BigDecimal, Boolean>(Compare.CompareLessOrEqual(),BigInteger::toBigDecimal, unmappedBigDecimal)),
        of(boolean, bigDecimalNullable, bigIntNullable, map<BigDecimal, BigDecimal, BigInteger, BigDecimal, Boolean>(Compare.CompareLessOrEqual(),unmappedBigDecimal, BigInteger::toBigDecimal)),
        of(boolean, javaDoubleNullable, bigDecimalNullable, map<Double, BigDecimal, BigDecimal, BigDecimal, Boolean>(Compare.CompareLessOrEqual(),Double::toBigDecimal, unmappedBigDecimal)),
        of(boolean, bigDecimalNullable, javaDoubleNullable, map<BigDecimal, BigDecimal, Double, BigDecimal, Boolean>(Compare.CompareLessOrEqual(),unmappedBigDecimal, Double::toBigDecimal)),
        of(boolean, javaIntNullable, bigDecimalNullable, map<Int, BigDecimal, BigDecimal, BigDecimal, Boolean>(Compare.CompareLessOrEqual(),Int::toBigDecimal, unmappedBigDecimal)),
        of(boolean, bigDecimalNullable, javaIntNullable, map<BigDecimal, BigDecimal, Int, BigDecimal, Boolean>(Compare.CompareLessOrEqual(),unmappedBigDecimal, Int::toBigDecimal)),
    ) {
        override fun toString(): String {
            return LessOrEquals::class.toString()
        }
    }
    object Greater : Evaluables(
        of(boolean, bigDecimalNullable, bigDecimalNullable, Compare.CompareGreater<BigDecimal>()),
        of(boolean, bigIntNullable, bigIntNullable, Compare.CompareGreater<BigInteger>()),
        of(boolean, javaDoubleNullable, javaDoubleNullable, Compare.CompareGreater<Double>()),
        of(boolean, javaIntNullable, javaIntNullable, Compare.CompareGreater<Int>()),
        of(boolean, bigIntNullable, bigDecimalNullable, map<BigInteger, BigDecimal, BigDecimal, BigDecimal, Boolean>(Compare.CompareGreater(),BigInteger::toBigDecimal, unmappedBigDecimal)),
        of(boolean, bigDecimalNullable, bigIntNullable, map<BigDecimal, BigDecimal, BigInteger, BigDecimal, Boolean>(Compare.CompareGreater(),unmappedBigDecimal, BigInteger::toBigDecimal)),
        of(boolean, javaDoubleNullable, bigDecimalNullable, map<Double, BigDecimal, BigDecimal, BigDecimal, Boolean>(Compare.CompareGreater(),Double::toBigDecimal, unmappedBigDecimal)),
        of(boolean, bigDecimalNullable, javaDoubleNullable, map<BigDecimal, BigDecimal, Double, BigDecimal, Boolean>(Compare.CompareGreater(),unmappedBigDecimal, Double::toBigDecimal)),
        of(boolean, javaIntNullable, bigDecimalNullable, map<Int, BigDecimal, BigDecimal, BigDecimal, Boolean>(Compare.CompareGreater(),Int::toBigDecimal, unmappedBigDecimal)),
        of(boolean, bigDecimalNullable, javaIntNullable, map<BigDecimal, BigDecimal, Int, BigDecimal, Boolean>(Compare.CompareGreater(),unmappedBigDecimal, Int::toBigDecimal)),
    ) {
        override fun toString(): String {
            return Greater::class.toString()
        }
    }
    object GreaterOrEquals : Evaluables(
        of(boolean, bigDecimalNullable, bigDecimalNullable, Compare.CompareGreaterOrEqual<BigDecimal>()),
        of(boolean, bigIntNullable, bigIntNullable, Compare.CompareGreaterOrEqual<BigInteger>()),
        of(boolean, javaDoubleNullable, javaDoubleNullable, Compare.CompareGreaterOrEqual<Double>()),
        of(boolean, javaIntNullable, javaIntNullable, Compare.CompareGreaterOrEqual<Int>()),
        of(boolean, bigIntNullable, bigDecimalNullable, map<BigInteger, BigDecimal, BigDecimal, BigDecimal, Boolean>(Compare.CompareGreaterOrEqual(),BigInteger::toBigDecimal, unmappedBigDecimal)),
        of(boolean, bigDecimalNullable, bigIntNullable, map<BigDecimal, BigDecimal, BigInteger, BigDecimal, Boolean>(Compare.CompareGreaterOrEqual(),unmappedBigDecimal, BigInteger::toBigDecimal)),
        of(boolean, javaDoubleNullable, bigDecimalNullable, map<Double, BigDecimal, BigDecimal, BigDecimal, Boolean>(Compare.CompareGreaterOrEqual(),Double::toBigDecimal, unmappedBigDecimal)),
        of(boolean, bigDecimalNullable, javaDoubleNullable, map<BigDecimal, BigDecimal, Double, BigDecimal, Boolean>(Compare.CompareGreaterOrEqual(),unmappedBigDecimal, Double::toBigDecimal)),
        of(boolean, javaIntNullable, bigDecimalNullable, map<Int, BigDecimal, BigDecimal, BigDecimal, Boolean>(Compare.CompareGreaterOrEqual(),Int::toBigDecimal, unmappedBigDecimal)),
        of(boolean, bigDecimalNullable, javaIntNullable, map<BigDecimal, BigDecimal, Int, BigDecimal, Boolean>(Compare.CompareGreaterOrEqual(),unmappedBigDecimal, Int::toBigDecimal)),
    ) {
        override fun toString(): String {
            return GreaterOrEquals::class.toString()
        }
    }
}