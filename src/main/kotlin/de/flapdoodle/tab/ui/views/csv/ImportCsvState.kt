package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.tab.model.Node
import java.nio.file.Path

data class ImportCsvState(
    val path: Path,
    val csvColumnNames: List<String> = emptyList(),
    val csvRows: List<List<String>> = emptyList(),
    val table: Node.Table<out Comparable<*>>? = null
) {
}