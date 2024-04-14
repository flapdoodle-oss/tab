package de.flapdoodle.tab.app.model.calculations.adapter.arithmetic

import de.flapdoodle.types.Either
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger

class PlusTest {
    @Test
    fun plusDefinedForAllTypeCombinations() {
        val valueSet = listOf(BigDecimal.ONE, BigInteger.ONE, 1.0, 1)
        val testee = Plus

        valueSet.forEach { first ->
            valueSet.forEach { second ->
                val eval = testee.find(listOf(first, second).toMutableList())
                Assertions.assertThat(eval)
                    .describedAs("${first.javaClass} + ${second.javaClass}")
                    .extracting(Either<*,*>::isLeft)
                    .isEqualTo(true)
            }
        }
    }

}