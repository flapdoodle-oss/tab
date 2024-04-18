package de.flapdoodle.tab.app.io.file

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Position
import de.flapdoodle.tab.app.model.calculations.Calculations
import de.flapdoodle.tab.app.model.data.Columns
import de.flapdoodle.tab.app.model.data.SingleValues
import kotlin.reflect.KClass

data class FileNode(
    val name: String,
    val position: Position,
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