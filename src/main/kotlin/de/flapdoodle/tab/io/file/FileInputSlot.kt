package de.flapdoodle.tab.io.file

data class FileInputSlot(
    val name: String,
    val mapTo: Set<FileVariable>,
    val source: FileSource? = null,
    val id: String,
    val color: FileColor
)