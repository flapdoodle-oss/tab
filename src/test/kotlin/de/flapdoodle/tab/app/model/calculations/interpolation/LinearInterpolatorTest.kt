package de.flapdoodle.tab.app.model.calculations.interpolation

import de.flapdoodle.tab.app.model.calculations.interpolation.linear.LinearInterpolation
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.MathContext

class LinearInterpolatorTest {

    private val mc = MathContext.DECIMAL64

    @Test
    fun singlePointInterpolation() {
        val testee = LinearInterpolator<Int, BigDecimal>(
            values =  mapOf(3 to BigDecimal.valueOf(10.0)),
            interpolation = LinearInterpolation.interpolation(Int::class, BigDecimal::class)!!
//            indexDistance = { s, e -> e.toDouble() - s.toDouble() },
//            deltaValue = { s, e -> e.subtract(s) },
//            interpolate = { s, delta, factor -> s.plus(delta.multiply(BigDecimal.valueOf(factor), mc))}
        )

        assertThat(testee.interpolated(0))
            .isEqualByComparingTo(BigDecimal.valueOf(10.0))
        assertThat(testee.interpolated(12))
            .isEqualByComparingTo(BigDecimal.valueOf(10.0))
        assertThat(testee.interpolated(15))
            .isEqualByComparingTo(BigDecimal.valueOf(10.0))
    }

    @Test
    fun twoPointInterpolation() {
        val testee = LinearInterpolator<Int, BigDecimal>(
            values =  mapOf(3 to BigDecimal.valueOf(10.0), 6 to BigDecimal.valueOf(20.0)),
            interpolation = LinearInterpolation.interpolation(Int::class, BigDecimal::class)!!
//            indexDistance = { s, e -> e.toDouble() - s.toDouble() },
//            deltaValue = { s, e -> e.subtract(s) },
//            interpolate = { s, delta, factor -> s.plus(delta.multiply(BigDecimal.valueOf(factor), mc))}
        )

        assertThat(testee.interpolated(0))
            .isEqualByComparingTo(BigDecimal.valueOf(0.0))
        assertThat(testee.interpolated(1))
            .isCloseTo(BigDecimal.valueOf(3.333333), Percentage.withPercentage(1.0))
        assertThat(testee.interpolated(9))
            .isEqualByComparingTo(BigDecimal.valueOf(30.0))
        assertThat(testee.interpolated(11))
            .isCloseTo(BigDecimal.valueOf(36.6666666), Percentage.withPercentage(1.0))
        assertThat(testee.interpolated(12))
            .isEqualByComparingTo(BigDecimal.valueOf(40.0))
        assertThat(testee.interpolated(15))
            .isEqualByComparingTo(BigDecimal.valueOf(50.0))
    }

    @Test
    fun threePointInterpolation() {
        val testee = LinearInterpolator<Int, BigDecimal>(
            values =  mapOf(3 to BigDecimal.valueOf(10.0), 6 to BigDecimal.valueOf(20.0), 12 to BigDecimal.valueOf(60.0)),
            interpolation = LinearInterpolation.interpolation(Int::class, BigDecimal::class)!!
//            indexDistance = { s, e -> e.toDouble() - s.toDouble() },
//            deltaValue = { s, e -> e.subtract(s) },
//            interpolate = { s, delta, factor -> s.plus(delta.multiply(BigDecimal.valueOf(factor)))}
        )

        assertThat(testee.interpolated(0))
            .isEqualByComparingTo(BigDecimal.valueOf(0.0))
        assertThat(testee.interpolated(1))
            .isCloseTo(BigDecimal.valueOf(3.333333), Percentage.withPercentage(1.0))
        assertThat(testee.interpolated(9))
            .isEqualByComparingTo(BigDecimal.valueOf(40.0))
        assertThat(testee.interpolated(11))
            .isCloseTo(BigDecimal.valueOf(53.3333), Percentage.withPercentage(1.0))
        assertThat(testee.interpolated(12))
            .isEqualByComparingTo(BigDecimal.valueOf(60.0))
        assertThat(testee.interpolated(15))
            .isEqualByComparingTo(BigDecimal.valueOf(80.0))
    }
}