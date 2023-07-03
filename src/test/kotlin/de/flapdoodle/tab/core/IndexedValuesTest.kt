package de.flapdoodle.tab.core

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class IndexedValuesTest {

    @Test
    fun playground() {
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
        println("-> $result")

        println("---> ${result.columns()}")
        println("---> ${result.indexList()}")
    }
}