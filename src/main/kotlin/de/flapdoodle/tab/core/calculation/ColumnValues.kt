package de.flapdoodle.tab.core.calculation

import de.flapdoodle.tab.core.values.Column

data class ColumnValues(
    val values: Set<ColumnValue<out Any>>
) {
    constructor(vararg values: ColumnValue<out Any>): this(setOf(*values))
    
    init {
        val columns = values.map { it.column }.toSet()
        require(columns.size == values.size) { "columns used more than once: $values" }
    }

    operator fun <C : Any> get(column: Column<C>): C? {
        val c: ColumnValue<C> = (values.singleOrNull { it.column == column }
            ?: throw IllegalArgumentException("column $column not expected: $values")) as ColumnValue<C>

        return c.value
    }
}