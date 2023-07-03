package de.flapdoodle.tab.core

data class Column<T : Any>(
    val type: ColumnType<T>,
    val id: Int = TypeCounter.nextId(Column::class, type::class)
)