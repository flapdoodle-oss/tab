package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.reflection.TypeInfo
import java.util.Locale

data class CsvCellConverter<T: Any>(
    val name: String,
    val type: TypeInfo<T>,
    val templates: Map<String, String> = emptyMap(),
    val conversion: (String, Locale) -> T,
)