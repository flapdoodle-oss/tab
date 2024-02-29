package de.flapdoodle.tab.app.ui.events

import de.flapdoodle.kfx.controls.grapheditor.GraphEditor
import de.flapdoodle.kfx.controls.grapheditor.events.Event
import de.flapdoodle.kfx.controls.grapheditor.events.EventListener
import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.calculations.InputSlot
import de.flapdoodle.tab.app.model.data.DataId
import de.flapdoodle.types.Either

class Event2ModelEvent(
    private val delegate: ModelEventListener,
    private val vertexIdMapper: (VertexId) -> Id<out Node>,
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
        }
    }
}