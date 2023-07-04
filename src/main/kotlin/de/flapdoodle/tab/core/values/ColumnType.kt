package de.flapdoodle.tab.core.values

import java.math.BigDecimal
import java.time.LocalDateTime

sealed class ColumnType<T> {
    object Text : ColumnType<String>()
    object Numeric : ColumnType<BigDecimal>()
    object Temporal : ColumnType<LocalDateTime>()
}