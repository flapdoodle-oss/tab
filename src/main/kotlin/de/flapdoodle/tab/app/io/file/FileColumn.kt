package de.flapdoodle.tab.app.io.file

import javafx.scene.paint.Color

data class FileColumn(
    val name: String,
    val valueType: String,
    val values: Map<String, String>,
    val id: String,
    val color: Color
)