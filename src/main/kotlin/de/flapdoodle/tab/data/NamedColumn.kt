package de.flapdoodle.tab.data

class NamedColumn<T : Any>(
    val name: String,
    val id: ColumnId<T>
)