package de.flapdoodle.tab.app.io.file

import de.flapdoodle.tab.app.model.Position

data class FileNode(
    val name: String,
    val position: Position,
    val id: String,
    val constants: Constants? = null,
    val table: Table? = null,
) {
    init {
        if (constants!=null) {
            require(table == null) {"constants and table set"}
        }
        if (table!=null) {
            require(constants == null) {"table and constants set"}
        }
    }

    data class Constants(
        val values: FileSingleValues,
    )

    data class Table(
        val indexType: String,
        val columns: FileColumns
    )
}