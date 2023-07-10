package de.flapdoodle.tab.core

import de.flapdoodle.tab.core.calculation.Calculation
import de.flapdoodle.tab.core.calculation.ColumnValue
import de.flapdoodle.tab.core.calculation.ColumnValues
import de.flapdoodle.tab.core.values.Column
import de.flapdoodle.tab.core.values.ColumnType
import de.flapdoodle.tab.core.values.IndexType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PlaygroundTest {

    @Test
    fun defineSomeCalculation() {
        val a = Column(ColumnType.Numeric)
        val b = Column(ColumnType.Numeric)

        val calculation = Calculation(
            indexType = IndexType.Numeric,
            columnType = ColumnType.Numeric,
            sources = setOf(a, b),
            function = { values -> values[a]!!.plus(values[b]!!) }
        )

        val columnValues = ColumnValues(
            ColumnValue(a, BigDecimal.ONE),
            ColumnValue(b, BigDecimal.valueOf(1.5)),
        )

        val result = calculation.function(columnValues)

        assertThat(result)
            .isEqualTo(BigDecimal.valueOf(2.5))
    }
}