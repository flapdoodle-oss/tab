package de.flapdoodle.tab.model.calculations.adapter.basic

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.eval.core.parser.TokenType
import de.flapdoodle.tab.model.calculations.Variables
import de.flapdoodle.tab.model.calculations.adapter.Evaluables
import de.flapdoodle.types.Either
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.time.ZoneId
import java.util.stream.Stream

class ComparablesTest {

    companion object {
        @JvmStatic
        fun compareNullablesList(): Stream<Arguments> {
            return listOf(
                CompareNullablesSet(Comparables.Less, Evaluated.value(1.0), Evaluated.value(1.0), false),
                CompareNullablesSet(Comparables.Less, Evaluated.value(0.9), Evaluated.value(1.0), true),
                CompareNullablesSet(Comparables.Less, Evaluated.value(0.9), Evaluated.value(BigDecimal.valueOf(1.0)), true),
                CompareNullablesSet(Comparables.Less, Evaluated.value(1.0), Evaluated.ofNull(Evaluables.javaDouble), false),
                CompareNullablesSet(Comparables.Less, Evaluated.ofNull(Evaluables.javaDouble), Evaluated.value(1.0), true),
                CompareNullablesSet(Comparables.Less, Evaluated.ofNull(Evaluables.javaDouble), Evaluated.ofNull(Evaluables.javaDouble), false),
                CompareNullablesSet(Comparables.Less, Evaluated.value(BigDecimal.valueOf(0.9)), Evaluated.value(BigInteger.ONE), true),

                CompareNullablesSet(Comparables.Greater, Evaluated.value(1.0), Evaluated.value(1.0), false),
                CompareNullablesSet(Comparables.Greater, Evaluated.value(0.9), Evaluated.value(1.0), false),
                CompareNullablesSet(Comparables.Greater, Evaluated.value(1.0), Evaluated.ofNull(Evaluables.javaDouble), true),
                CompareNullablesSet(Comparables.Greater, Evaluated.ofNull(Evaluables.javaDouble), Evaluated.value(1.0), false),
                CompareNullablesSet(Comparables.Greater, Evaluated.ofNull(Evaluables.javaDouble), Evaluated.ofNull(Evaluables.javaDouble), false),
            ).stream().map(Arguments::of)
        }

        @JvmStatic
        fun testees(): Stream<Arguments> {
            return listOf(
                Comparables.Less,
                Comparables.LessOrEquals,
                Comparables.Greater,
                Comparables.GreaterOrEquals,
            ).stream().map(Arguments::of)
        }
    }

    data class CompareNullablesSet<T>(val testee: Evaluables, val left: Evaluated<T>, val right: Evaluated<T>, val expected: Boolean)

    @ParameterizedTest
    @MethodSource("compareNullablesList")
    fun compareNullables(set: CompareNullablesSet<Comparable<Any>>) {
        val testee = set.testee
        val values = listOf(set.left, set.right).toMutableList()

        val eval = testee.find(values)
        assertThat(eval)
            .describedAs("$set - eval")
            .extracting(Either<*,*>::isLeft)
            .isEqualTo(true)

        val variableResolver = VariableResolver.empty()
        val evaluationContext = EvaluationContext.builder()
            .mathContext(MathContext.DECIMAL128)
            .zoneId(ZoneId.systemDefault())
            .build()

        val result = eval.left().evaluate(variableResolver, evaluationContext, Token.of(0,"test", TokenType.FUNCTION), values)
        
        assertThat(result).isInstanceOf(Evaluated::class.java)
            .describedAs("$set - expected")
            .extracting(Evaluated<out Any>::wrapped)
            .isEqualTo(set.expected)
    }

    @ParameterizedTest
    @MethodSource("testees")
    fun compareDefinedForAllTypeCombinations(testee: Evaluables) {
        val valueSet = listOf(
            BigDecimal.ONE, BigInteger.ONE, 1.0, 1
        )

        valueSet.forEach { first ->
            valueSet.forEach { second ->
                val eval = testee.find(listOf(Evaluated.value(first), Evaluated.value(second)).toMutableList())
                
                assertThat(eval)
                    .describedAs("$testee -> ${first.javaClass} + ${second.javaClass}")
                    .extracting(Either<*,*>::isLeft)
                    .isEqualTo(true)
            }
        }
    }

}