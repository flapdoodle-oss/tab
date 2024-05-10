package de.flapdoodle.tab.model.calculations.adapter.arithmetic

import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.tab.model.calculations.adapter.arithmetic.Minus
import de.flapdoodle.types.Either
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger

class MinusTest {
    @Test
    fun minusDefinedForAllTypeCombinations() {
        val valueSet = listOf(BigDecimal.ONE, BigInteger.ONE, 1.0, 1)
        val testee = Minus

        valueSet.forEach { first ->
            valueSet.forEach { second ->
                val eval = testee.find(listOf(Evaluated.value(first), Evaluated.value(second)).toMutableList())
                Assertions.assertThat(eval)
                    .describedAs("${first.javaClass} + ${second.javaClass}")
                    .extracting(Either<*,*>::isLeft)
                    .isEqualTo(true)
            }
        }
    }

}