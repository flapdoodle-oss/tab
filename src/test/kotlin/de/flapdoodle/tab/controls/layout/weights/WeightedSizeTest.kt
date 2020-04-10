package de.flapdoodle.tab.controls.layout.weights

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class WeightedSizeTest {

  @Test
  fun `same weights must share space equal`() {
    val src = listOf(
        WeightedSize(weight = 1.0, min = 0.0, max = Double.MAX_VALUE),
        WeightedSize(weight = 1.0, min = 0.0, max = Double.MAX_VALUE)
    )

    val result = WeightedSize.distribute(100.0, src)

    assertThat(result).containsExactly(50.0, 50.0)
  }

  @Test
  fun `same weights must share space equal if enough space left`() {
    val src = listOf(
        WeightedSize(weight = 1.0, min = 75.0, max = Double.MAX_VALUE),
        WeightedSize(weight = 1.0, min = 0.0, max = Double.MAX_VALUE)
    )

    val result = WeightedSize.distribute(100.0, src)

    assertThat(result).containsExactly(75.0, 25.0)
  }

  @Test
  fun `same weights must share space equal if not too much space left`() {
    val src = listOf(
        WeightedSize(weight = 1.0, min = 0.0, max = 25.0),
        WeightedSize(weight = 1.0, min = 0.0, max = Double.MAX_VALUE)
    )

    val result = WeightedSize.distribute(100.0, src)

    assertThat(result).containsExactly(25.0, 75.0)
  }
}