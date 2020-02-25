package de.flapdoodle.tab.data.values

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class ValuesTest {
  @Test
  fun `rows is zero if empty`() {
    val testee = Values(String::class)

    Assertions.assertThat(testee.size()).isEqualTo(0)
  }

  @Test
  fun `rows is zero if empty again`() {
    val testee = Values(String::class)
        .set(17,"foo")
        .set(17, null)

    Assertions.assertThat(testee.size()).isEqualTo(0)
  }

  @Test
  fun `rows is one if index zero is filled`() {
    val testee = Values(String::class)
        .set(0,"some")

    Assertions.assertThat(testee.size()).isEqualTo(1)
  }

  @Test
  fun `rows contains max indexed value`() {
    val testee = Values(String::class)
        .set(3,"some")

    Assertions.assertThat(testee.size()).isEqualTo(4)
  }

}