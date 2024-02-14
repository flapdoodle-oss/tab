package de.flapdoodle.tab.core.calculation

import de.flapdoodle.tab.core.values.Column

interface ColumnValueLookup {
    operator fun <C : Any> get(column: Column<C>): C?
}