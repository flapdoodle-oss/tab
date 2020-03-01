package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.values.ValueMap
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
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
}