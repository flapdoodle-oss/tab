package de.flapdoodle.tab.app.io.file

import javafx.scene.paint.Color

class FileColor(
    val red: Double,
    val green: Double,
    val blue: Double,
    val opacity: Double
) {

    fun toColor(): Color {
        return Color.color(red, green, blue, opacity)
    }

    companion object {
        fun from(color: Color): FileColor {
            return FileColor(
                color.red,
                color.green,
                color.blue,
                color.opacity
            )
        }
    }
}