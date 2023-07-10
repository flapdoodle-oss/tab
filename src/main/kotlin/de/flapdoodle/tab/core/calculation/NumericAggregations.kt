package de.flapdoodle.tab.core.calculation

import de.flapdoodle.tab.core.values.ColumnType
import de.flapdoodle.tab.core.values.IndexType
import java.math.BigDecimal

object NumericAggregations {
    fun <I : Any> sum(indexType: IndexType<I>): Aggregation<I, BigDecimal> {
        return Aggregation(
            indexType,
            ColumnType.Numeric
        ) { list -> if (list.isNotEmpty()) list.sumOf { it.second } else null }
    }

    fun <I : Any> avg(indexType: IndexType<I>): Aggregation<I, BigDecimal> {
        return Aggregation(
            indexType,
            ColumnType.Numeric
        ) { list -> if (list.isNotEmpty()) (list.sumOf { it.second }.divide(list.size.toBigDecimal())) else null }
    }
}