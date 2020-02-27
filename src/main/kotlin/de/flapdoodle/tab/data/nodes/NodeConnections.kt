package de.flapdoodle.tab.data.nodes

data class NodeConnections(
    val id: NodeId<out ConnectableNode>,
    val variableMappings: List<VariableMapping<out Any>> = emptyList()
)