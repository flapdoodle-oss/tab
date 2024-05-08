package de.flapdoodle.tab.ui

import de.flapdoodle.kfx.bindings.Subscriptions
import de.flapdoodle.kfx.collections.Mapping
import de.flapdoodle.kfx.controls.grapheditor.Edge
import de.flapdoodle.kfx.controls.grapheditor.GraphEditor
import de.flapdoodle.kfx.controls.grapheditor.Vertex
import de.flapdoodle.kfx.controls.grapheditor.slots.Position
import de.flapdoodle.kfx.controls.grapheditor.slots.Slot
import de.flapdoodle.kfx.controls.grapheditor.types.EdgeId
import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.extensions.unsubscribeOnDetach
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.DataId
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.ui.commands.Command
import de.flapdoodle.tab.ui.events.Event2ModelEvent
import de.flapdoodle.tab.ui.events.ModelEventListener
import de.flapdoodle.tab.ui.views.DefaultNodeUIAdapterFactory
import de.flapdoodle.tab.ui.views.NodeUIAdapter
import de.flapdoodle.types.Either
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point2D
import javafx.scene.layout.AnchorPane

class Tab2ModelAdapter(
    model: ReadOnlyObjectProperty<Tab2Model>,
    modelEventListener: ModelEventListener,
    val modelChangeListener: ModelChangeListener
) : AnchorPane() {
    private val debug = false

    private val graphEditor = GraphEditor(eventListener = Event2ModelEvent(
        delegate = modelEventListener,
        vertexIdMapper = ::nodeOfVertex,
        slotIdMapper = ::outputOrInputOfSlot
    )
    ).withAnchors(all = 10.0)
    private val nodeUIAdapterFactory = DefaultNodeUIAdapterFactory

    private class VertexAndContent(val vertex: Vertex, val content: NodeUIAdapter)
    private val vertexMapping = Mapping<Id<out de.flapdoodle.tab.model.Node>, VertexId, VertexAndContent>()
    private val slotMapping = Mapping<DataId, SlotId, Slot>()
    private val inputMapping = Mapping<Id<InputSlot<*>>, SlotId, Slot>()
    private val edgeMapping = Mapping<Output2Input, EdgeId, Edge>()

    private val selectedVertices = SimpleObjectProperty<Set<Id<out de.flapdoodle.tab.model.Node>>>(emptySet())
    private val selectedEdges = SimpleObjectProperty<Set<Output2Input>>(emptySet())

    init {
        children.add(graphEditor)
        apply(Action.syncActions(Tab2Model(), model.value))

        unsubscribeOnDetach {
            model.subscribe { old, current ->
                apply(Action.syncActions(old, current))
            }
        }
    }

    private fun nodeOfVertex(vertexId: VertexId): Id<out de.flapdoodle.tab.model.Node> {
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


    fun selectedNodesProperty(): ReadOnlyProperty<Set<Id<out de.flapdoodle.tab.model.Node>>> = selectedVertices
    fun selectedEdgesProperty(): ReadOnlyProperty<Set<Output2Input>> = selectedEdges

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
            if (debug) println("action: $action")

            when (action) {
                is Action.AddNode -> {
                    graphEditor.addVertex(Vertex(action.node.name).also { vertex ->
                        val vertexContent = nodeUIAdapterFactory.adapterOf(action.node, modelChangeListener)
                        vertex.layoutPosition = Point2D(action.node.position.x, action.node.position.y)
                        vertex.content = vertexContent
                        vertexMapping.add(action.node.id, vertex.vertexId,
                            VertexAndContent(vertex, vertexContent)
                        )
                        Subscriptions.add(vertex, vertex.selectedProperty().subscribe { it -> changeSelection(action.node.id, it) })
                    })
                }

                is Action.ChangeNode -> {
                    vertexMapping.with(action.id) {
                        it.vertex.nameProperty().value = action.node.name
                        it.vertex.layoutPosition = Point2D(action.node.position.x, action.node.position.y)
                        it.content.update(action.node)
                    }
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
                        is Column<*,*> -> Slot(action.output.name, Slot.Mode.OUT, Position.RIGHT, action.output.color)
                        is SingleValue<*> -> Slot(action.output.name, Slot.Mode.OUT, Position.RIGHT, action.output.color)
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
                is Action.ChangeOutput -> {
                    vertexMapping.with(action.id) { vertexMapping ->
                        slotMapping.with(action.output) {
                            // TODO hier fehlt vieles
                            println("TODO should do something: $action")
                        }
                    }
                }

                is Action.AddInput -> {
                    val slot = Slot(action.input.name, Slot.Mode.IN, Position.LEFT, action.input.color)
                    inputMapping.add(action.input.id, slot.id, slot)
                    vertexMapping.with(action.id) {
                        it.vertex.addConnector(slot)
                    }
                }

                is Action.ChangeInput -> {
                    vertexMapping.with(action.id) { vertexMapping ->
                        inputMapping.with(action.input) {
                            // TODO hier fehlt vieles
                            println("TODO should do something: $action")
                        }
                    }
                }

                is Action.RemoveInput -> {
                    inputMapping.remove(action.input) { slot ->
                        vertexMapping.with(action.id) {
                            it.vertex.removeConnector(slot.id)
                        }
                    }
                }
                is Action.AddConnection -> {
                    vertexMapping.with(action.source.node) { start ->
                        vertexMapping.with(action.id) { end ->
                            slotMapping.with(action.source.dataId()) { startSlot ->
                                inputMapping.with(action.input) { input ->
                                    graphEditor.addEdge(Edge(
                                        VertexSlotId(start.vertex.vertexId, startSlot.id),
                                        VertexSlotId(end.vertex.vertexId, input.id),
                                    ).also { edge ->
                                        val output2Input = Output2Input(action.source, action.id, action.input)
                                        edgeMapping.add(output2Input, edge.edgeId, edge)
                                        Subscriptions.add(edge, edge.selectedProperty().subscribe { it -> changeSelection(output2Input, it) })
                                    })
                                }
                            }
                        }
                    }
                }

                is Action.RemoveConnection -> {
                    val output2Input = Output2Input(action.source, action.id, action.input)
                    edgeMapping.remove(output2Input) {
                        graphEditor.removeEdge(it)
                        changeSelection(output2Input, false)
                        Subscriptions.unsubscribeAll(it)
                    }
                }
            }
        }
    }

    private fun changeSelection(vertex: Id<out de.flapdoodle.tab.model.Node>, selection: Boolean) {
        val current = selectedVertices.get()
        selectedVertices.value = if (selection) {
            current + vertex
        } else {
            current - vertex
        }
    }

    data class Output2Input(
        val source: Source,
        val id: Id<out de.flapdoodle.tab.model.Node>,
        val input: Id<InputSlot<*>>
    )

    private fun changeSelection(edge: Output2Input, selection: Boolean) {
        val current = selectedEdges.get()
        selectedEdges.value = if (selection) {
            current + edge
        } else {
            current - edge
        }
    }

}