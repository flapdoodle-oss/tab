package de.flapdoodle.tab.ui.views.csv

data class ColumnFormat(
    val name: String,
    val converter: ColumnConverter<out Any>
)