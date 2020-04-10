package de.flapdoodle.tab.controls.layout

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LimitTest {

  @Test
  fun `must add limits even if value is double_max`() {
    val a = Limit(0.0, 100.0)
    val b = Limit(10.0, Double.MAX_VALUE)

    val c = a + b
    assertThat(c.min).isEqualTo(0.0 + 10.0)
    assertThat(c.max).isEqualTo(Double.MAX_VALUE)
  }

  @Test
  fun `must add limits even if both values are double_max`() {
    val a = Limit(0.0, Double.MAX_VALUE)
    val b = Limit(10.0, Double.MAX_VALUE)

    val c = a + b
    assertThat(c.min).isEqualTo(0.0 + 10.0)
    assertThat(c.max).isEqualTo(Double.MAX_VALUE)
  }
}