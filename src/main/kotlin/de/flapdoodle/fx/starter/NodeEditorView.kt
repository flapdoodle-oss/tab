package de.flapdoodle.fx.starter

import de.flapdoodle.fx.graph.AutoScrollingWindow
import de.flapdoodle.fx.graph.GraphView
import de.flapdoodle.fx.graph.PanningWindow
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.View
import tornadofx.button
import tornadofx.vbox

class NodeEditorView : View("Node Editor") {
    override val root = vbox {
        val graphView = GraphView()
        val panningWindow = object : AutoScrollingWindow() {
            init {
                setAutoScrollingEnabled(true)
                setContent(graphView)
            }
        }


        children+= panningWindow
        button("click me") {

        }

        VBox.setVgrow(graphView, Priority.SOMETIMES)
    }
}