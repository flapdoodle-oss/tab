package de.flapdoodle.tab.core.calculation

import de.flapdoodle.tab.core.values.Column
import de.flapdoodle.tab.core.values.ColumnType
import de.flapdoodle.tab.core.values.IndexType

data class CalculationNode<I: Any, C: Any>(
    val indexType: IndexType<I>,
    val columnType: ColumnType<C>,
    val sources: Set<Column<out Any>>,
    val function: Calculator<C>
)
