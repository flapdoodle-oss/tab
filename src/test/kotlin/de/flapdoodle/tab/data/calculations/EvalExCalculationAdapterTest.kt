package de.flapdoodle.tab.data.calculations

import com.ezylang.evalex.Expression
import de.flapdoodle.tab.data.values.Input
import de.flapdoodle.tab.data.values.ValueMap
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class EvalExCalculationAdapterTest {

  @Test
  fun `adapter will work with big decimal`() {
    val calculation = EvalExCalculationAdapter("a*2")
    val result = calculation.calculate(ValueMap().add("a", BigDecimal("10.0")))

    assertThat(result)
        .isBetween(BigDecimal.valueOf(19.0), BigDecimal.valueOf(21.0))
        .isEqualTo(BigDecimal.valueOf(20.0))
  }

  @Test
  fun `expect variable a`() {
    val calculation = EvalExCalculationAdapter("a*2")

    assertThat(calculation.variables())
        .containsExactlyInAnyOrder(Input.Variable(BigDecimal::class,"a"))
  }

  @Test
  fun `expect variables a and b`() {
    val calculation = EvalExCalculationAdapter("a*2+b")

    assertThat(calculation.variables())
        .containsExactlyInAnyOrder(Input.Variable(BigDecimal::class,"a"), Input.Variable(BigDecimal::class,"b"))
  }

  @Test
  fun `functions must work`() {
    val calculation = EvalExCalculationAdapter("SIN(a)*2+b")

    assertThat(calculation.variables())
        .containsExactlyInAnyOrder(Input.Variable(BigDecimal::class,"a"), Input.Variable(BigDecimal::class,"b"))

    val result = calculation.calculate(ValueMap().add("a", BigDecimal("10.0")).add("b", BigDecimal("5.0")))

    assertThat(result).isEqualTo(BigDecimal("5.3"))
  }

  @Test
  fun `from original doc`() {
    // Using pre-created BigDecimals for variables
    // Using pre-created BigDecimals for variables
    val a = BigDecimal("2.4")
    val b = BigDecimal("9.235")
    val result = Expression("SQRT(a^2 + b^2)")
        .with("a", a)
        .and("b", b)
        .evaluate() // 9.5591845

    assertThat(result.numberValue).isCloseTo(BigDecimal("9.5417618"), Percentage.withPercentage(99.0))
  }

  @Test
  fun `SIN must work`() {
    // Using pre-created BigDecimals for variables
    // Using pre-created BigDecimals for variables
    val a = BigDecimal("2.4")
    val b = BigDecimal("9.235")
    val result = Expression("SIN(a)*b")
        .with("a", a)
        .and("b", b)
        .evaluate() // 9.5591845

    assertThat(result.numberValue).isCloseTo(BigDecimal("0.3867216"), Percentage.withPercentage(99.0))
  }
}