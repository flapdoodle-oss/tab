package de.flapdoodle.tab.model.calculations.interpolation

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.calculations.interpolation.linear.LinearInterpolation
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.MathContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

class LinearInterpolatorTest {

    private val mc = MathContext.DECIMAL64

    @Test
    fun singlePointInterpolation() {
        val testee = LinearInterpolator(
            values =  mapOf(3 to BigDecimal.valueOf(10.0)),
            interpolation = LinearInterpolation.interpolation(TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(BigDecimal::class.javaObjectType))!!,
            valueType = TypeInfo.of(BigDecimal::class.javaObjectType)
        )

        assertThat(testee.interpolated(0).wrapped())
            .isEqualByComparingTo(BigDecimal.valueOf(10.0))
        assertThat(testee.interpolated(12).wrapped())
            .isEqualByComparingTo(BigDecimal.valueOf(10.0))
        assertThat(testee.interpolated(15).wrapped())
            .isEqualByComparingTo(BigDecimal.valueOf(10.0))
    }

    @Test
    fun twoPointInterpolation() {
        val testee = LinearInterpolator(
            values =  mapOf(3 to BigDecimal.valueOf(10.0), 6 to BigDecimal.valueOf(20.0)),
            interpolation = LinearInterpolation.interpolation(TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(BigDecimal::class.javaObjectType))!!,
            valueType = TypeInfo.of(BigDecimal::class.javaObjectType)
        )

        assertThat(testee.interpolated(0).wrapped())
            .isEqualByComparingTo(BigDecimal.valueOf(0.0))
        assertThat(testee.interpolated(1).wrapped())
            .isCloseTo(BigDecimal.valueOf(3.333333), Percentage.withPercentage(1.0))
        assertThat(testee.interpolated(9).wrapped())
            .isEqualByComparingTo(BigDecimal.valueOf(30.0))
        assertThat(testee.interpolated(11).wrapped())
            .isCloseTo(BigDecimal.valueOf(36.6666666), Percentage.withPercentage(1.0))
        assertThat(testee.interpolated(12).wrapped())
            .isEqualByComparingTo(BigDecimal.valueOf(40.0))
        assertThat(testee.interpolated(15).wrapped())
            .isEqualByComparingTo(BigDecimal.valueOf(50.0))
    }

    @Test
    fun threePointInterpolation() {
        val testee = LinearInterpolator(
            values =  mapOf(3 to BigDecimal.valueOf(10.0), 6 to BigDecimal.valueOf(20.0), 12 to BigDecimal.valueOf(60.0)),
            interpolation = LinearInterpolation.interpolation(TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(BigDecimal::class.javaObjectType))!!,
            valueType = TypeInfo.of(BigDecimal::class.javaObjectType)
        )

        assertThat(testee.interpolated(0).wrapped())
            .isEqualByComparingTo(BigDecimal.valueOf(0.0))
        assertThat(testee.interpolated(1).wrapped())
            .isCloseTo(BigDecimal.valueOf(3.333333), Percentage.withPercentage(1.0))
        assertThat(testee.interpolated(9).wrapped())
            .isEqualByComparingTo(BigDecimal.valueOf(40.0))
        assertThat(testee.interpolated(11).wrapped())
            .isCloseTo(BigDecimal.valueOf(53.3333), Percentage.withPercentage(1.0))
        assertThat(testee.interpolated(12).wrapped())
            .isEqualByComparingTo(BigDecimal.valueOf(60.0))
        assertThat(testee.interpolated(15).wrapped())
            .isEqualByComparingTo(BigDecimal.valueOf(80.0))
    }

    @Test
    fun dateInterpolation() {
        val now = LocalDate.of(2023, Month.MAY, 2)

        val testee = LinearInterpolator(
            values =  mapOf(
                now to BigDecimal.valueOf(10.0),
                now.plusDays(2) to BigDecimal.valueOf(20.0)
            ),
            valueType = TypeInfo.of(BigDecimal::class.javaObjectType),
            interpolation = LinearInterpolation.interpolation(
                TypeInfo.of(LocalDate::class.java),
                TypeInfo.of(BigDecimal::class.javaObjectType),
            )!!
        )

        assertThat(testee.interpolated(now.plusDays(1)).wrapped())
            .isEqualByComparingTo(BigDecimal.valueOf(15.0))
    }

    @Test
    fun enumInterpolation() {
        val testee = LinearInterpolator(
            values =  mapOf(
                Month.FEBRUARY to BigDecimal.valueOf(10.0),
                Month.APRIL to BigDecimal.valueOf(20.0)
            ),
            valueType = TypeInfo.of(BigDecimal::class.javaObjectType),
            interpolation = LinearInterpolation.interpolation(
                TypeInfo.of(Month::class.java),
                TypeInfo.of(BigDecimal::class.javaObjectType),
            )!!
        )

        assertThat(testee.interpolated(Month.MARCH).wrapped())
            .isEqualByComparingTo(BigDecimal.valueOf(15.0))
    }
}