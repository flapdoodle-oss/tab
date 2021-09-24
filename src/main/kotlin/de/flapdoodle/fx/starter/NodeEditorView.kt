package de.flapdoodle.fx.starter

import de.flapdoodle.fx.graph.Content
import de.flapdoodle.fx.graph.GraphView
import de.flapdoodle.fx.graph.PanningWindow
import de.flapdoodle.fx.layout.panning.VirtualPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.View
import tornadofx.button
import tornadofx.onLeftClick
import tornadofx.vbox

class NodeEditorView : View("Node Editor") {
    override val root = vbox {
        val graphView = GraphView()
        val dummy = Content()
        val window = object : PanningWindow() {
            init {
                setContent(dummy)
            }
        }

        children+= window
        children+= VirtualPane().apply {
            setContent(Content())
        }

        button("click me") {
            onLeftClick {
                window.scrollTo(-50.0, -50.0)
            }
        }

        VBox.setVgrow(graphView, Priority.SOMETIMES)
    }
}