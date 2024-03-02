package de.flapdoodle.tab.app.ui.views.calculations

import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.ui.ModelChangeListener
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox

class CalculationsPane<K: Comparable<K>>(
    node: Node.Calculated<K>,
    val modelChangeListener: ModelChangeListener
) : VBox() {
    val nodeId = node.id

    init {
        children.add(Label("XYZ"))
    }
    
}