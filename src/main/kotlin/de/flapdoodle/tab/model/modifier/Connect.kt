package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.DataId
import de.flapdoodle.tab.model.data.SingleValueId
import de.flapdoodle.tab.types.change

data class Connect(
    val start: Node,
    val end: Node.Calculated<out Comparable<*>>,
    val dataId: DataId,
    val inputId: Id<InputSlot<*>>
): Modifier() {

    override fun modify(nodes: List<Node>): List<Node> {
        return connect(nodes, start, dataId, end, inputId)
    }

    companion object {
        fun map(nodes: List<Node>, change: Change.Connect): Connect {
            require(change.startDataOrInput.isLeft != change.endDataOrInput.isLeft) { "can not connect input to input/output to output" }
            require(change.startId != change.endId) { "can not connect to itself" }

            val nodesById = nodes.associateBy { it.id }

            val start: Node
            val end: Node
            val dataId: DataId
            val inputId: Id<InputSlot<*>>

            if (change.startDataOrInput.isLeft) {
                // dataId 2 input
                start = requireNotNull(nodesById[change.startId]) { "start not found: ${change.startId}"}
                end = requireNotNull(nodesById[change.endId]) { "end not found: ${change.endId}" }
                dataId = change.startDataOrInput.left()
                inputId = change.endDataOrInput.right()
            } else {
                // input 2 dataId
                start = requireNotNull(nodesById.get(change.endId)) { "start not found: ${change.endId}" }
                end = requireNotNull(nodesById.get(change.startId)) { "end not found: ${change.startId}" }
                dataId = change.endDataOrInput.left()
                inputId = change.startDataOrInput.right()
            }

            require(end is Node.Calculated<out Comparable<*>>) { "wrong node type: $end" }

            return Connect(start, end, dataId, inputId)
        }

        private fun <K: Comparable<K>> connect(
            nodes: List<Node>,
            start: Node,
            dataId: DataId,
            end: Node.Calculated<K>,
            inputId: Id<InputSlot<*>>
        ): List<Node> {
            val source = when (dataId) {
                is ColumnId -> {
                    require(start is Node.HasColumns<out Comparable<*>>) { "wrong source node type: $start" }
                    val inputSlot = end.calculations.inputSlot(inputId)
                    require(inputSlot.isColumnReference || start.indexType == end.indexType) { "index type mismatch: ${start.indexType} != ${end.indexType}" }
                    val startWithIndexType = start as Node.HasColumns<K>
                    Source.ColumnSource(start.id, dataId, startWithIndexType.indexType)
                }

                is SingleValueId -> Source.ValueSource(start.id, dataId)
            }

            val connected = end.connect(inputId, source)

            return nodes.change(Node::id, connected.id) { connected }
        }

    }
}