package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.values.Variable

data class Connections(
    val variableMappings: List<VariableMapping<out Any>> = emptyList()
) {
  fun <T: Any> add(variable: Variable<T>, columnId: ColumnId<T>): Connections {
    require(!variableMappings.any { it.variable==variable }) { "$variable already connected"}
    return copy(variableMappings  = variableMappings + VariableMapping(columnId, variable))
  }
}