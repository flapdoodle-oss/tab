package de.flapdoodle.tab.core.values

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class IndexedColumnValuesTest {

    private val now = LocalDateTime.now()
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)

    @Test
    fun aggregateAllChanges() {
        val emptyTestee = IndexedColumnValues(IndexType.Temporal, ColumnType.Text)

        val changed = emptyTestee.change(
            now to "Now",
            now.plusDays(1) to "+1",
            now.minusDays(2) to "-1",
        )

        Assertions.assertThat(changed.get(now, now.plusDays(2), now.plusDays(1)))
            .containsExactly("Now", null, "+1")

        Assertions.assertThat(changed.indexList())
            .containsExactly(
                now.minusDays(2),
                now,
                now.plusDays(1),
            )
    }

    @Test
    fun removeEntry() {
        val emptyTestee = IndexedColumnValues(IndexType.Temporal, ColumnType.Text)

        val changed = emptyTestee.change(
            now to "Now"
        ).change(
            now to null
        )

        Assertions.assertThat(changed.get(now))
            .containsExactly(null)

        Assertions.assertThat(changed.indexList())
            .isEmpty()
    }
}