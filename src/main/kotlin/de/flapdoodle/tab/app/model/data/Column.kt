package de.flapdoodle.tab.app.model.data

import de.flapdoodle.tab.data.values.ValueContainer

data class Column<T: Any>(
    val type: NamedType<T>,
    val values: ValueContainer<T>
)