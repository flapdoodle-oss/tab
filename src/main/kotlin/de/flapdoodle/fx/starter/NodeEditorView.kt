package de.flapdoodle.fx.starter

import de.flapdoodle.fx.clone.*
import de.flapdoodle.fx.graph.Content
import de.flapdoodle.fx.graph.GraphView
import de.flapdoodle.fx.graph.PanningWindow
import de.flapdoodle.fx.layout.panning.VirtualPane
import javafx.event.EventHandler
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import tornadofx.*

class NodeEditorView : View("Node Editor") {
    override val root = vbox {
        val graphView = GraphView()
        val dummy = Content()
        val window = object : PanningWindow() {
            init {
                setContent(dummy)
            }
        }

//        children+= window
//        children+= VirtualPane().apply {
//            setContent(Content())
//        }


        val graphEditorProperties = BoxFactory.sampleProperties()
        val graphEditorView = GraphEditorView(graphEditorProperties)
        graphEditorView.add(BoxFactory.sampleBox(graphEditorProperties))

//        val scrollPane = Pane() //de.flapdoodle.fx.clone.PanningWindow()
        val scrollPane = object : de.flapdoodle.fx.clone.PanningWindow() {
            init {
                setContent(graphEditorView)
                setEditorProperties(graphEditorProperties)
            }
        }
        scrollPane.setPrefSize(600.0, 600.0)
//        scrollPane.setEditorProperties(graphEditorProperties)


//        scrollPane.children.add(BoxFactory.sampleBox(graphEditorProperties))

        children+=scrollPane

        button("click me") {
            onLeftClick {
                window.scrollTo(-50.0, -50.0)
            }
        }

        VBox.setVgrow(graphView, Priority.SOMETIMES)

//        scene.getStylesheets().add(graphEditorView.styleResource);
    }
}