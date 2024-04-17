package de.flapdoodle.tab.app.io.file

import javafx.scene.paint.Color

data class FileSingleValue(
    val name: String,
    val valueType: String,
    val value: String? = null,
    val id: FileDataId.SingleValueId,
    val color: Color
)