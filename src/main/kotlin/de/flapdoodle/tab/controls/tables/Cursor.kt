package de.flapdoodle.tab.controls.tables

data class Cursor<T: Any>(
    val column: SmartColumn<T, out Any>,
    val row: Int
) {
}