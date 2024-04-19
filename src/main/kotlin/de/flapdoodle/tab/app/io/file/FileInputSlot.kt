package de.flapdoodle.tab.app.io.file

import javafx.scene.paint.Color

data class FileInputSlot(
    val name: String,
    val mapTo: Set<FileVariable>,
    val source: FileSource? = null,
    val id: String,
    val color: Color
)