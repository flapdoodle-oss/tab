package de.flapdoodle.tab.model.calculations.adapter.arithmetic

import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.tab.model.calculations.adapter.arithmetic.Multiply
import de.flapdoodle.types.Either
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger

class MultiplyTest {
    @Test
    fun multiplyDefinedForAllTypeCombinations() {
        val valueSet = listOf(BigDecimal.ONE, BigInteger.ONE, 1.0, 1)
        val testee = Multiply

        valueSet.forEach { first ->
            valueSet.forEach { second ->
                val eval = testee.find(listOf(Evaluated.value(first), Evaluated.value(second)).toMutableList())
                assertThat(eval)
                    .describedAs("${first.javaClass} * ${second.javaClass}")
                    .extracting(Either<*,*>::isLeft)
                    .isEqualTo(true)
            }
        }
    }

}