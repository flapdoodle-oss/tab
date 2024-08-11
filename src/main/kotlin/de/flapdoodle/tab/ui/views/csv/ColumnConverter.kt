package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.reflection.TypeInfo

data class ColumnConverter<T: Any>(
    val type: TypeInfo<T>,
    val converter: (String, String?) -> T,
    val format: String? = null
)