package de.flapdoodle.tab.graph.nodes.connections

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.values.Variable
import de.flapdoodle.tab.graph.events.IsMarker

data class VariableInput<T : Any>(val variable: Variable<T>) : IsMarker