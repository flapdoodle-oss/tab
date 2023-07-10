package de.flapdoodle.tab.core.calculation

import de.flapdoodle.tab.core.values.ColumnType
import de.flapdoodle.tab.core.values.IndexType

data class Aggregation<I : Any, C : Any>(
    val indexType: IndexType<I>,
    val columnType: ColumnType<C>,
    val function: (List<Pair<I,C>>) -> C?
)