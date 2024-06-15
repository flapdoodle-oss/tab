package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.connections.Source

data class RemoveConnection(
    val nodeId: Id<Node.Calculated<*>>,
    val input: Id<InputSlot<*>>,
    val source: Source
) : SingleNodeModifier<Node.Calculated<*>>(
    id = nodeId,
    change = { n -> n.removeConnectionFrom(input,source.node,source.id) }
) {

    companion object {
        fun removeSource(nodes: List<Node>, source: Id<out Node>): List<RemoveConnection> {
            return nodes.filterIsInstance<Node.Calculated<*>>()
                .flatMap { calculated -> removeConnections(calculated, source) }
        }

        //VisibleForTesting
        internal fun removeConnections(calculated: Node.Calculated<*>, source: Id<out Node>): List<RemoveConnection> {
            return calculated.calculations.inputs()
                .flatMap { inputSlot ->
                    if (inputSlot.source != null && inputSlot.source.node == source) {
                        listOf(RemoveConnection(calculated.id, inputSlot.id, inputSlot.source))
                    } else {
                        emptyList()
                    }
                }
        }
    }
}