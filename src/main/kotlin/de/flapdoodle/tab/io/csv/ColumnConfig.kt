package de.flapdoodle.tab.io.csv

data class ColumnConfig(
    val headerRows: Int = 1,
    val indexColumn: Int,
    val converter: Map<Int, CsvConverter<out Any>>
) {
    init {
        require(headerRows >= 0) { "headerRows must be positive." }
        require(converter.isNotEmpty()) { "no converter" }
        require(converter.containsKey(indexColumn)) { "no converter for index column $indexColumn" }
    }
}
