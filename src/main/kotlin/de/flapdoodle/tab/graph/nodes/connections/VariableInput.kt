package de.flapdoodle.tab.graph.nodes.connections

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Variable
import de.flapdoodle.tab.graph.events.IsMarker

data class VariableInput<T : Any>(
    val id: NodeId<out ConnectableNode>,
    val variable: Variable<T>
) : IsMarker