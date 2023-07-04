package de.flapdoodle.tab.core.values

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class IndexedValuesTest {

    @Test
    fun aggregateAllChanges() {
        val testee = IndexedValues(IndexType.Temporal)
        val columnA = Column(ColumnType.Text)
        val columnB = Column(ColumnType.Numeric)

        val now = LocalDateTime.now()
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        val result = testee.put(
            columnA,
            now to "What?",
            now.plusDays(1) to "Why?",
            now.minusDays(2) to "One",
        ).put(
            columnB,
            now.plusDays(3) to BigDecimal.valueOf(123)
        )
        
        assertThat(result.columns())
            .containsExactlyInAnyOrder(columnA, columnB)
        
        assertThat(result.indexList())
            .containsExactly(
                now.minusDays(2),
                now,
                now.plusDays(1),
                now.plusDays(3),
            )
    }
}