package de.flapdoodle.tab.data

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ColumnTest {

  @Test
  fun `rows is zero if empty`() {
    val testee = Column(ColumnId(String::class,"name"))

    assertThat(testee.size()).isEqualTo(0)
  }

  @Test
  fun `rows is zero if empty again`() {
    val testee = Column(ColumnId(String::class,"name"))
        .set(17,"foo")
        .set(17, null)

    assertThat(testee.size()).isEqualTo(0)
  }

  @Test
  fun `rows is one if index zero is filled`() {
    val testee = Column(ColumnId(String::class,"name"))
        .set(0,"some")

    assertThat(testee.size()).isEqualTo(1)
  }

  @Test
  fun `rows contains max indexed value`() {
    val testee = Column(ColumnId(String::class,"name"))
        .set(3,"some")

    assertThat(testee.size()).isEqualTo(4)
  }

}