package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.values.Input

data class Connection<T: Any>(
    val variable: Input<T>,
    val sourceNode: NodeId<out ConnectableNode>,
    val columnConnection: ColumnConnection<T>
)