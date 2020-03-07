package de.flapdoodle.tab.data

import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.Connections
import de.flapdoodle.tab.data.nodes.NodeId

data class NodeConnections(
    private val connections: Map<NodeId<out ConnectableNode>, Connections> = emptyMap()
) {
}