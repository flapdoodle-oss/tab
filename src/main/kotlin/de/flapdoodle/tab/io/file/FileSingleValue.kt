package de.flapdoodle.tab.io.file

data class FileSingleValue(
    val name: String,
    val valueType: String,
    val value: String? = null,
    val id: String,
    val color: FileColor
)