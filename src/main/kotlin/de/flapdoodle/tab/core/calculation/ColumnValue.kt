package de.flapdoodle.tab.core.calculation

import de.flapdoodle.tab.core.values.Column

data class ColumnValue<C: Any>(
    val column: Column<C>,
    val value: C?
)