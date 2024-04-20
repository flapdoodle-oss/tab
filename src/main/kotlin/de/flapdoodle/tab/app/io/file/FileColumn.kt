package de.flapdoodle.tab.app.io.file

data class FileColumn(
    val name: String,
    val valueType: String,
    val values: Map<String, String>,
    val id: String,
    val color: FileColor
)