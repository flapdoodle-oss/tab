package de.flapdoodle.tab.app.model.calculations.adapter.arithmetic

import de.flapdoodle.types.Either
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger

class DivideTest {
    @Test
    fun divisionDefinedForAllTypeCombinations() {
        val valueSet = listOf(BigDecimal.ONE, BigInteger.ONE, 1.0, 1)
        val testee = Divide

        valueSet.forEach { dividend ->
            valueSet.forEach { divisor ->
                val eval = testee.find(listOf(dividend, divisor).toMutableList())
                assertThat(eval)
                    .describedAs("${dividend.javaClass} / ${divisor.javaClass}")
                    .extracting(Either<*,*>::isLeft)
                    .isEqualTo(true)
            }
        }
    }
}