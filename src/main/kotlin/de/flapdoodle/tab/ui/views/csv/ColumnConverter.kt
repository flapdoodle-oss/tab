package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.reflection.TypeInfo

data class ColumnConverter<T: Any>(
    val type: TypeInfo<T>,
    val format: String? = null,
    val validatingConverter: ValidatingConverter<T>
)