package de.flapdoodle.fx.graph

import javafx.scene.control.Control
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class GraphView : Control() {
    private val skin=GraphViewSkin(this)

    init {
        val first = Rectangle(30.0, 20.0).apply {
            this.fill = Color.RED
        }

        val second = Rectangle(30.0, 20.0).apply {
            this.fill = Color.BLUE
            this.x=0.0
        }

        children.addAll(first, second)
    }

    override fun createDefaultSkin() = skin
}