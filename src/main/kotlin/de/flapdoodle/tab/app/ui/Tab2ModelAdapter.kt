package de.flapdoodle.tab.app.ui

import de.flapdoodle.kfx.bindings.Subscriptions
import de.flapdoodle.kfx.controls.grapheditor.GraphEditor
import de.flapdoodle.kfx.controls.grapheditor.Vertex
import de.flapdoodle.kfx.controls.grapheditor.events.EventListener
import de.flapdoodle.kfx.controls.grapheditor.types.EdgeId
import de.flapdoodle.kfx.controls.graphmodeleditor.GraphEditorModelAdapter
import de.flapdoodle.kfx.controls.graphmodeleditor.model.Edge
import de.flapdoodle.kfx.controls.graphmodeleditor.model.VertexContent
import de.flapdoodle.kfx.controls.graphmodeleditor.types.VertexId
import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.extensions.unsubscribeOnDetach
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.ui.commands.Command
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point2D
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane

class Tab2ModelAdapter(
    model: ReadOnlyObjectProperty<Tab2Model>
) : AnchorPane() {
    private val graphEditor = GraphEditor(eventListener = EventListener { graphEditor, event -> false }).withAnchors(all = 10.0)

    private class VertexAndContent(val vertex: Vertex, val content: javafx.scene.Node)
    private val vertexMapping = Mapping<Id<out Node>, de.flapdoodle.kfx.controls.grapheditor.types.VertexId, VertexAndContent>()
//    private val edgeMapping = Mapping<Edge<V>, EdgeId, de.flapdoodle.kfx.controls.grapheditor.Edge>()

    private val selectedVertices = SimpleObjectProperty<Set<Id<out Node>>>(emptySet())
//    private val selectedEdges = SimpleObjectProperty<Set<Edge<V>>>(emptySet())

    init {
        children.add(graphEditor)
        apply(Action.syncActions(Tab2Model(), model.value))

        unsubscribeOnDetach {
            model.subscribe { old, current ->
                apply(Action.syncActions(old, current))
            }
        }
    }

    fun selectedNodesProperty(): ReadOnlyProperty<Set<Id<out Node>>> = selectedVertices
//    fun selectedEdgesProperty(): ReadOnlyProperty<Set<Edge<V>>> = selectedEdges

    fun execute(command: Command) {
        val mapped = when (command) {
            is Command.Abort -> de.flapdoodle.kfx.controls.grapheditor.commands.Command.Abort()
            is Command.AskForPosition -> de.flapdoodle.kfx.controls.grapheditor.commands.Command.AskForPosition(command.onSuccess)
//            is Command.FindVertex -> {
//                val pos = vertexMapping[command.vertex]!!.vertex.layoutPosition
//                de.flapdoodle.kfx.controls.grapheditor.commands.Command.PanTo(pos,command.onSuccess)
//            }
        }
        graphEditor.execute(mapped)
    }


    private fun apply(action: List<Action>) {
        action.forEach { action ->
            println("action: $action")

            when (action) {
                is Action.AddNode -> {
                    graphEditor.addVertex(Vertex(action.node.name).also { vertex ->
                        val vertexContent = Button("wooHoo")
                        vertex.layoutPosition = Point2D(action.node.position.x, action.node.position.y)
                        vertex.content = vertexContent
                        vertexMapping.add(action.node.id, vertex.vertexId,
                            VertexAndContent(vertex, vertexContent)
                        )
                        Subscriptions.add(vertex, vertex.selectedProperty().subscribe { it -> changeSelection(action.node.id, it) })
                    })
                }

                is Action.RemoveNode -> {
                    vertexMapping.remove(action.id) {
                        graphEditor.removeVertex(it.vertex)
                        changeSelection(action.id, false)
                        Subscriptions.unsubscribeAll(it.vertex)
                    }

                }

                else -> {
                    println("not implemented: $action")
                }
            }
        }
    }

    private fun changeSelection(vertex: Id<out Node>, selection: Boolean) {
        val current = selectedVertices.get()
        selectedVertices.value = if (selection) {
            current + vertex
        } else {
            current - vertex
        }
    }

//    private fun changeSelection(egde: de.flapdoodle.kfx.controls.graphmodeleditor.model.Edge<V>, selection: Boolean) {
//        val current = selectedEdges.get()
//        selectedEdges.value = if (selection) {
//            current + egde
//        } else {
//            current - egde
//        }
//    }

}