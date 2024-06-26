package de.flapdoodle.tab.io.file

import de.flapdoodle.tab.model.Position
import de.flapdoodle.tab.model.Size

data class FileNode(
    val name: String,
    val short: String?,
    val description: String?,
    val position: Position,
    val size: Size? = null,
    val id: String,
    val constants: Constants? = null,
    val table: Table? = null,
    val calculated: Calculated? = null
) {
    init {
        if (constants!=null) {
            require(table == null) {"constants and table set"}
            require(calculated == null) {"constants and calculated set"}
        }
        if (table!=null) {
            require(constants == null) {"table and constants set"}
            require(calculated == null) {"table and calculated set"}
        }
        if (calculated!=null) {
            require(constants == null) {"calculated and constants set"}
            require(table == null) {"calculated and table set"}
        }
    }

    data class Constants(
        val values: FileSingleValues,
    )

    data class Table(
        val indexType: String,
        val columns: FileColumns
    )

    data class Calculated(
        val indexType: String,
        val calculations: FileCalculations,
//        val calculations: Calculations<K> = Calculations(indexType),
        val columns: FileColumns,
        val values: FileSingleValues
    )
}