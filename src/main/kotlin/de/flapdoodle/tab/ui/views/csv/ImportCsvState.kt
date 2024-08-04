package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.tab.model.Node
import java.nio.file.Path

data class ImportCsvState(
    val path: Path,
    val table: Node.Table<out Comparable<*>>? = null
) {
}