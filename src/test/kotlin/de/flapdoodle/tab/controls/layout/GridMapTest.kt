package de.flapdoodle.tab.controls.layout

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GridMapTest {

  @Test
  fun `map columns must give matching entries`() {
    val testee = GridMap(mapOf(
        GridMap.Pos(0, 0) to "(0,0)",
        GridMap.Pos(1, 0) to "(1,0)",
        GridMap.Pos(1, 1) to "(1,1)"
    ))

    val result = testee.mapColumns { list -> list.joinToString(separator = "|") }

    assertThat(result)
        .containsExactly("(0,0)","(1,0)|(1,1)")
  }

  @Test
  fun `map rows must give matching entries`() {
    val testee = GridMap(mapOf(
        GridMap.Pos(0, 0) to "(0,0)",
        GridMap.Pos(1, 0) to "(1,0)",
        GridMap.Pos(1, 1) to "(1,1)"
    ))

    val result = testee.mapRows { list -> list.joinToString(separator = "|") }

    assertThat(result)
        .containsExactly("(0,0)|(1,0)","(1,1)")
  }

}