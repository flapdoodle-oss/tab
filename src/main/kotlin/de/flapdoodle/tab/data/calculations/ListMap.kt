package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.VariableMapping
import de.flapdoodle.tab.data.values.Input
import de.flapdoodle.tab.data.values.Values

data class ListMap(
    private val map: Map<Input.List<out Any>, Values<Any>>
) {

  fun isValidFor(variable: Input.List<out Any>): Boolean {
    return map.keys.contains(variable)
  }

  fun asLookup(): Aggregation.VariableLookup {
    return object : Aggregation.VariableLookup {
      override fun <T : Any> get(variable: Input.List<T>): List<T?>? {
        @Suppress("UNCHECKED_CAST")
        val values = map[variable] as Values<T>
        return values.asList()
      }
    }
  }

  companion object {
    fun variableMap(data: Data, variables: List<VariableMapping<out Any>>): ListMap {
      return ListMap(variables.map {
        require(it.columnConnection is ColumnConnection.Aggregate) { "not implemented: ${it.columnConnection}"}
        require(it.variable is Input.List) { "not implemented: ${it.variable}"}
        it.variable to data[it.columnConnection.columnId]
      }.toMap())
    }
  }
}