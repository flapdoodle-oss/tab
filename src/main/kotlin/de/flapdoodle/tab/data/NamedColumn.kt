package de.flapdoodle.tab.data

data class NamedColumn<T : Any>(
    val name: String,
    val id: ColumnId<T>
)