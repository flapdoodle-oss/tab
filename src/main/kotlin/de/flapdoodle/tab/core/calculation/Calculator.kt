package de.flapdoodle.tab.core.calculation

fun interface Calculator<C: Any> {
    fun calculate(lookup: ColumnValueLookup): C?
}