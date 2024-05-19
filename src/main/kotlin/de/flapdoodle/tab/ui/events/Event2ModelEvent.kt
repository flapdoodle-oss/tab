package de.flapdoodle.tab.ui.events

import de.flapdoodle.kfx.controls.grapheditor.GraphEditor
import de.flapdoodle.kfx.controls.grapheditor.events.Event
import de.flapdoodle.kfx.controls.grapheditor.events.EventListener
import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Position
import de.flapdoodle.tab.model.Size
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.data.DataId
import de.flapdoodle.types.Either

class Event2ModelEvent(
    private val delegate: ModelEventListener,
    private val vertexIdMapper: (VertexId) -> Id<out de.flapdoodle.tab.model.Node>,
    private val slotIdMapper: (SlotId) -> Either<DataId, Id<InputSlot<*>>>
) : EventListener {
    override fun onEvent(graphEditor: GraphEditor, event: Event): Boolean {
        return when (event) {
            is Event.TryToConnect -> {
                val nodeId = vertexIdMapper(event.start.vertexId)
                val dataOrInput = slotIdMapper(event.start.slotId)

                delegate.onEvent(ModelEvent.TryToConnect(nodeId, dataOrInput))
            }

            is Event.TryToConnectTo -> {
                val startNodeId = vertexIdMapper(event.start.vertexId)
                val startDataOrInput = slotIdMapper(event.start.slotId)
                val endNodeId = vertexIdMapper(event.end.vertexId)
                val endDataOrInput = slotIdMapper(event.end.slotId)

                delegate.onEvent(ModelEvent.TryToConnectTo(startNodeId, startDataOrInput, endNodeId, endDataOrInput))
            }

            is Event.ConnectTo -> {
                val startNodeId = vertexIdMapper(event.start.vertexId)
                val startDataOrInput = slotIdMapper(event.start.slotId)
                val endNodeId = vertexIdMapper(event.end.vertexId)
                val endDataOrInput = slotIdMapper(event.end.slotId)

                delegate.onEvent(ModelEvent.ConnectTo(startNodeId, startDataOrInput, endNodeId, endDataOrInput))
            }
            is Event.VertexMoved -> {
                val nodeId = vertexIdMapper(event.vertexId)
                delegate.onEvent(
                    ModelEvent.VertexMoved(
                        nodeId,
                        Position(event.layoutPosition.x, event.layoutPosition.y)
                    )
                )
            }
            is Event.VertexResized -> {
                val nodeId = vertexIdMapper(event.vertexId)
                delegate.onEvent(
                    ModelEvent.VertexResized(
                        nodeId,
                        Position(event.layoutPosition.x, event.layoutPosition.y),
                        Size(event.size.width, event.size.height)
                    )
                )
            }
        }
    }
}