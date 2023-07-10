package de.flapdoodle.tab.core.calculation

import de.flapdoodle.tab.core.values.ColumnType
import de.flapdoodle.tab.core.values.IndexType

data class Calculation<I: Any, C: Any>(
    val indexType: IndexType<I>,
    val columnType: ColumnType<C>,
    // name -> column mapping
    // lookup?
)
