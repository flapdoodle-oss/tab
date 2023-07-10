package de.flapdoodle.tab.core.values

import de.flapdoodle.tab.core.calculation.Aggregation
import de.flapdoodle.tab.core.calculation.NumericAggregations
import java.math.BigDecimal
import java.time.LocalDateTime

sealed class ColumnType<T : Any> {
    abstract fun aggregations(): List<Aggregation<out Any, T>>

    object Text : ColumnType<String>() {
        override fun aggregations(): List<Aggregation<out Any, String>> = emptyList()
    }

    object Numeric : ColumnType<BigDecimal>() {
        override fun aggregations(): List<Aggregation<out Any, BigDecimal>> = listOf(
            NumericAggregations.sum(IndexType.Numeric),
            NumericAggregations.sum(IndexType.Temporal),
            NumericAggregations.avg(IndexType.Numeric),
            NumericAggregations.avg(IndexType.Temporal)
        )
    }

    object Temporal : ColumnType<LocalDateTime>() {
        override fun aggregations(): List<Aggregation<out Any, LocalDateTime>> = emptyList()
    }
}