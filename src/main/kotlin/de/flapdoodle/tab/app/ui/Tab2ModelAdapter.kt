package de.flapdoodle.tab.app.ui

import de.flapdoodle.kfx.bindings.Subscriptions
import de.flapdoodle.kfx.controls.grapheditor.GraphEditor
import de.flapdoodle.kfx.controls.grapheditor.Vertex
import de.flapdoodle.kfx.controls.grapheditor.events.EventListener
import de.flapdoodle.kfx.controls.grapheditor.slots.Position
import de.flapdoodle.kfx.controls.grapheditor.slots.Slot
import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.extensions.unsubscribeOnDetach
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.model.calculations.InputSlot
import de.flapdoodle.tab.app.model.data.Column
import de.flapdoodle.tab.app.model.data.DataId
import de.flapdoodle.tab.app.model.data.SingleValue
import de.flapdoodle.tab.app.ui.commands.Command
import de.flapdoodle.tab.app.ui.events.Event2ModelEvent
import de.flapdoodle.tab.app.ui.events.ModelEventListener
import de.flapdoodle.types.Either
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point2D
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane

class Tab2ModelAdapter(
    model: ReadOnlyObjectProperty<Tab2Model>,
    modelEventListener: ModelEventListener
) : AnchorPane() {
    private val graphEditor = GraphEditor(eventListener = Event2ModelEvent(
        delegate = modelEventListener,
        vertexIdMapper = ::nodeOfVertex,
        slotIdMapper = ::outputOrInputOfSlot
    )).withAnchors(all = 10.0)

    private class VertexAndContent(val vertex: Vertex, val content: javafx.scene.Node)
    private val vertexMapping = Mapping<Id<out Node>, VertexId, VertexAndContent>()
    private val slotMapping = Mapping<DataId, SlotId, Slot>()
    private val inputMapping = Mapping<Id<InputSlot<*>>, SlotId, Slot>()
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

    private fun nodeOfVertex(vertexId: VertexId): Id<out Node> {
        return requireNotNull(vertexMapping.key(vertexId)) { "could not get node for $vertexId" }
    }

    private fun outputOrInputOfSlot(slotId: SlotId): Either<DataId, Id<InputSlot<*>>> {
        val dataId = slotMapping.key(slotId)
        if (dataId!=null) {
            return Either.left(dataId)
        }
        val inputId = inputMapping.key(slotId)
        return Either.right(requireNotNull(inputId) { "could not find mapping for $slotId"})
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
//            println("action: $action")

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

                is Action.AddOutput -> {
                    val slot = when (action.output) {
                        is Column<*,*> -> Slot(action.output.name, Slot.Mode.OUT, Position.RIGHT)
                        is SingleValue<*> -> Slot(action.output.name, Slot.Mode.OUT, Position.RIGHT)
                    }
                    slotMapping.add(action.output.id, slot.id, slot)
                    vertexMapping.with(action.id) {
                        it.vertex.addConnector(slot)
                    }
                }

                is Action.RemoveOutput -> {
                    slotMapping.remove(action.output) { slot ->
                        vertexMapping.with(action.id) {
                            it.vertex.removeConnector(slot.id)
                        }
                    }
                }

                is Action.AddInput -> {
                    val slot = Slot(action.input.name, Slot.Mode.IN, Position.LEFT)
                    inputMapping.add(action.input.id, slot.id, slot)
                    vertexMapping.with(action.id) {
                        it.vertex.addConnector(slot)
                    }
                }

                is Action.RemoveInput -> {
                    inputMapping.remove(action.input) { slot ->
                        vertexMapping.with(action.id) {
                            it.vertex.removeConnector(slot.id)
                        }
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