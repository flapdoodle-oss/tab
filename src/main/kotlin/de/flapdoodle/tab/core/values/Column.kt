package de.flapdoodle.tab.core.values

import de.flapdoodle.tab.types.TypeCounter

data class Column<T : Any>(
    val type: ColumnType<T>,
    val id: Int = TypeCounter.nextId(Column::class, type::class)
)