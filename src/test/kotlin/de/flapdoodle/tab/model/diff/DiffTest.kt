package de.flapdoodle.tab.model.diff

import de.flapdoodle.tab.model.diff.Diff
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DiffTest {

    @Test
    fun diff() {
        val old = listOf(1 to "A", 2 to "B", 3 to "C", 4 to "D")
        val new = listOf(1 to "A", 5 to "C", 3 to "c")
        val diff = Diff.diff(old, new, Pair<Int, String>::first)

        assertThat(diff.same)
            .containsExactlyInAnyOrder(1 to "A")
        assertThat(diff.changed)
            .containsExactlyInAnyOrder((3 to "C") to (3 to "c"))
        assertThat(diff.new)
            .containsExactlyInAnyOrder(5 to "C")
        assertThat(diff.removed)
            .containsExactlyInAnyOrder(2 to "B", 4 to "D")
    }
}