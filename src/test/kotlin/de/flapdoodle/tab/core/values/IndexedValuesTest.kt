package de.flapdoodle.tab.core.values

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class IndexedValuesTest {

    private val now = LocalDateTime.now()
        .withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)

    @Test
    fun aggregateAllChanges() {
        val emptyTestee = IndexedValues(IndexType.Temporal)
        val columnA = Column(ColumnType.Text)
        val columnB = Column(ColumnType.Numeric)

        val changed = emptyTestee.change(
            columnA,
            now to "Now",
            now.plusDays(1) to "+1",
            now.minusDays(2) to "-1",
        ).change(
            columnB,
            now.plusDays(3) to BigDecimal.valueOf(123)
        )

        assertThat(changed.get(columnA, now, now.plusDays(2), now.plusDays(1)))
            .containsExactly("Now", null, "+1")

        assertThat(changed.columns())
            .containsExactlyInAnyOrder(columnA, columnB)
        
        assertThat(changed.indexList())
            .containsExactly(
                now.minusDays(2),
                now,
                now.plusDays(1),
                now.plusDays(3),
            )
    }

    @Test
    fun removeEntry() {
        val emptyTestee = IndexedValues(IndexType.Temporal)
        val columnA = Column(ColumnType.Text)

        val changed = emptyTestee.change(
            columnA,
            now to "Now"
        ).change(
            columnA,
            now to null
        )

        assertThat(changed.get(columnA, now))
            .containsExactly(null)

        assertThat(changed.columns())
            .isEmpty()

        assertThat(changed.indexList())
            .isEmpty()
    }
}