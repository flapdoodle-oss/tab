package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.tab.app.model.Node
import javafx.geometry.Orientation
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

abstract class NodeUIAdapter(): VBox() {
    abstract fun update(node: Node)

}