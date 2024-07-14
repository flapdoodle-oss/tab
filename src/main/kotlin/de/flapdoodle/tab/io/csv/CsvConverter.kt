package de.flapdoodle.tab.io.csv

import de.flapdoodle.reflection.TypeInfo

data class CsvConverter<T: Any>(
    val type: TypeInfo<T>,
    val converter: (String) -> T?
) {
}