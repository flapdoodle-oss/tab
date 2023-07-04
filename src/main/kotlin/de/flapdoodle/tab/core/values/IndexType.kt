package de.flapdoodle.tab.core.values

import java.math.BigDecimal
import java.time.LocalDateTime

sealed class IndexType<T> {
    abstract fun comparator(): Comparator<T>

    object Numeric : IndexType<BigDecimal>() {
        override fun comparator(): Comparator<BigDecimal> {
            return Comparator.naturalOrder()
        }
    }
    object Temporal : IndexType<LocalDateTime>() {
        override fun comparator(): Comparator<LocalDateTime> {
            return Comparator.naturalOrder()
        }

    }
}