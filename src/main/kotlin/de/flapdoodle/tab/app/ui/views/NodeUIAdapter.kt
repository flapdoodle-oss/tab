package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.tab.app.model.Node
import javafx.scene.layout.StackPane

abstract class NodeUIAdapter(): StackPane() {
    abstract fun update(node: Node)

}