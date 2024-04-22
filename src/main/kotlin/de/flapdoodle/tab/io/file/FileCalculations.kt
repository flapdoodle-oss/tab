package de.flapdoodle.tab.io.file

data class FileCalculations(
    // TODO kann weg
//    val indexType: String,
    val aggregations: List<FileCalculation>,
    val tabular: List<FileCalculation>,
    val inputs: List<FileInputSlot>
)