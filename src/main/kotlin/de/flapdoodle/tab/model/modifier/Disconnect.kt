package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.DataId
import de.flapdoodle.tab.types.one

data class Disconnect(
    val nodeId: Id<Node.Calculated<*>>,
    val input: Id<InputSlot<*>>,
    val source: Source
) : SingleNodeModifier<Node.Calculated<*>>(
    id = nodeId,
    change = { n -> n.removeConnectionFrom(input,source.node,source.id) }
) {

    companion object {
        fun removeSource(nodes: List<Node>, source: Id<out Node>): List<Disconnect> {
            return nodes.filterIsInstance<Node.Calculated<*>>()
                .flatMap { calculated -> removeConnections(calculated, source) }
        }

        //VisibleForTesting
        internal fun removeConnections(calculated: Node.Calculated<*>, source: Id<out Node>): List<Disconnect> {
            return calculated.calculations.inputs()
                .flatMap { inputSlot ->
                    if (inputSlot.source != null && inputSlot.source.node == source) {
                        listOf(Disconnect(calculated.id, inputSlot.id, inputSlot.source))
                    } else {
                        emptyList()
                    }
                }
        }

        fun removeSource(nodes: List<Node>, id: Id<out Node>, dataId: DataId): List<Disconnect> {
            return nodes.filterIsInstance<Node.Calculated<*>>()
                .flatMap { calculated -> removeSource(calculated, id, dataId) }
        }

        internal fun removeSource(calculated: Node.Calculated<*>, id: Id<out Node>, dataId: DataId): List<Disconnect> {
            return calculated.calculations.inputs()
                .flatMap { inputSlot ->
                    if (inputSlot.source != null && inputSlot.source.node == id && inputSlot.source.dataId() == dataId) {
                        listOf(Disconnect(calculated.id, inputSlot.id, inputSlot.source))
                    } else {
                        emptyList()
                    }
                }
        }

        fun removeConnection(nodes: List<Node>, endId: Id<out Node>, input: Id<InputSlot<*>>, source: Source): Modifier {
            val node = nodes.one { it.id == endId }
            require(node is Node.Calculated<out Comparable<*>>) {"node is not calculated: $node"}
            return Disconnect(node.id, input, source)
        }


    }
}
