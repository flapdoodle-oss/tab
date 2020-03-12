package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.VariableMapping
import de.flapdoodle.tab.data.values.Input
import de.flapdoodle.tab.data.values.Values

data class VariableMap(
    private val map: Map<Input.Variable<out Any>, Values<Any>>
) {

  fun isValidFor(variables: Set<Input.Variable<out Any>>): Boolean {
    return map.keys.containsAll(variables)
  }

  fun size(variables: Set<Input.Variable<out Any>>): Int {
    require(map.keys.containsAll(variables)) { "not all variables are mapped: ${map.keys} < $variables" }
    return map.filterKeys { variables.contains(it) }
        .values.map { it.size() }
        .max() ?: 0
  }

  fun lookupFor(index: Int): Calculation.VariableLookup {
    return object : Calculation.VariableLookup {
      override fun <T : Any> get(variable: Input.Variable<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return (map[variable] as Values<T>)[index]
      }
    }
  }

  companion object {
    fun variableMap(data: Data, variables: List<VariableMapping<out Any>>): VariableMap {
      return VariableMap(variables.map {
        require(it.columnConnection is ColumnConnection.ColumnValues) { "not implemented: ${it.columnConnection}"}
        require(it.variable is Input.Variable) { "not implemented: ${it.variable}"}
        it.variable to data[it.columnConnection.columnId]
      }.toMap())
    }
  }
}