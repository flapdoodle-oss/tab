package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.tab.app.model.Node
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.layout.StackPane

abstract class NodeUIAdapter(): StackPane() {
    abstract fun update(node: Node)

    init {
        children.add(Button("clickMe").apply {
            onAction = EventHandler {
                println("clicked")
                it.consume()
            }
        })
    }
}