package de.flapdoodle.tab.types

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChangeTest {

    @Test
    fun someChange() {
        val old = listOf(1 to "A", 2 to "B", 3 to "C")
        val new = listOf(1 to "A", 2 to "b", 4 to "X")
        val change = Change.change(old, new, Pair<Int, String>::first)

        assertThat(change.notChanged)
            .containsExactlyInAnyOrder(1 to "A")
        assertThat(change.modified)
            .containsExactlyInAnyOrder((2 to "B") to (2 to "b"))
        assertThat(change.added)
            .containsExactlyInAnyOrder(4 to "X")
        assertThat(change.removed)
            .containsExactlyInAnyOrder(3 to "C")
    }
}