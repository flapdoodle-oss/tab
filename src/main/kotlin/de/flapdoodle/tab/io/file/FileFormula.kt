package de.flapdoodle.tab.io.file

data class FileFormula(
    val expression: String,
    val variables: Set<FileVariable>
)