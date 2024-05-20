package de.flapdoodle.tab.ui.views

import javafx.scene.layout.StackPane

abstract class NodeUIAdapter(): StackPane() {
    abstract fun update(node: de.flapdoodle.tab.model.Node)
}