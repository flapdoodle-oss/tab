package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.values.Variable

data class Connections(
    val variableMappings: List<VariableMapping<out Any>> = emptyList()
) {
  fun <T : Any> add(variable: Variable<T>, columnConnection: ColumnConnection<T>): Connections {
    require(!variableMappings.any { it.variable == variable }) { "$variable already connected" }
    return copy(variableMappings = variableMappings + VariableMapping(columnConnection, variable))
  }

  fun filterInvalidColumns(missingColumns: Set<ColumnId<out Any>>): Connections {
    val filteredMappings = variableMappings.filter {
      !missingColumns.contains(it.columnConnection.columnId)
    }
    if (filteredMappings.size != variableMappings.size) {
      return copy(variableMappings = filteredMappings)
    }
    return this
  }

  fun filterInvalidInputs(hasInputs: HasInputs): Connections {
    val validVariables = hasInputs.variables()
    val filteredMappings = variableMappings.filter {
      validVariables.contains(it.variable)
    }
    if (filteredMappings.size != variableMappings.size) {
      return copy(variableMappings = filteredMappings)
    }
    return this
  }
}