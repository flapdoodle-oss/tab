package de.flapdoodle.tab.app.io.file

data class FileFormula(
    val expression: String,
    val variables: Set<FileVariable>
)