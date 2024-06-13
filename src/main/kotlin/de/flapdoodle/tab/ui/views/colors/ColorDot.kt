package de.flapdoodle.tab.ui.views.colors

import de.flapdoodle.kfx.layout.grid.TableCell
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class ColorDot(
    color: Color
) : StackPane() {
    private val colorProperty = SimpleObjectProperty<Color>(color)
    private val rectangle = Rectangle(10.0, 10.0).apply {
        fillProperty().bind(colorProperty)
    }

    init {
        children.add(rectangle)
        maxWidth = USE_PREF_SIZE
        maxHeight = USE_PREF_SIZE
    }


    fun set(color: Color) {
        colorProperty.value = color
    }

    fun get(): Color {
        return colorProperty.value
    }

    companion object {
        fun <T> tableCell(initialValue: T, mapper: (T) -> Color?): TableCell<T, ColorDot> {
            return TableCell.with(ColorDot(Color.GRAY))
                .map(mapper)
                .updateWith { colorDot, color -> colorDot.set(color ?: Color.GRAY) }
                .initializedWith(initialValue)
        }
    }
}