package de.flapdoodle.tab.app.io.file

data class FileSource(
    val node: String,
    val nodeType: String,
    val id: String,
    val dataId: FileDataId,
)