package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.values.Variable

data class Connection<T: Any>(
    val variable: Variable<T>,
    val sourceNode: NodeId<out ConnectableNode>,
    val columnColumn: ColumnId<T>
)