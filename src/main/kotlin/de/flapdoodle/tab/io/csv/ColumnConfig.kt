package de.flapdoodle.tab.io.csv

import de.flapdoodle.tab.model.Title

data class ColumnConfig<K: Comparable<K>>(
    val name: Title,
    val headerRows: Int = 1,
    val indexConverter: Pair<Int, CsvConverter<K>>,
    val converter: Map<Int, CsvConverter<out Any>>
) {
    init {
        require(headerRows >= 0) { "headerRows must be positive." }
        require(converter.isNotEmpty()) { "no converter" }
        require(!converter.containsKey(indexConverter.first)) { "column collision" }
    }
}
