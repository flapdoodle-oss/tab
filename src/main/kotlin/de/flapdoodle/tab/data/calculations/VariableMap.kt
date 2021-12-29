package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.VariableMapping
import de.flapdoodle.tab.data.values.Input
import de.flapdoodle.tab.data.values.ValueContainer

data class VariableMap(
    private val map: Map<Input.Variable<out Any>, ValueContainer<Any>>,
    private val isSingleValue: Set<Input.Variable<out Any>>
) {

  fun isValidFor(variables: Set<Input.Variable<out Any>>): Boolean {
    return map.keys.containsAll(variables)
  }

  fun areSingleValues(variables: Set<Input.Variable<out Any>>): Boolean {
    return isSingleValue.containsAll(variables)
  }

  fun size(variables: Set<Input.Variable<out Any>>): Int {
    require(map.keys.containsAll(variables)) { "not all variables are mapped: ${map.keys} < $variables" }
    return map.filterKeys { variables.contains(it) }
        .values.map { it.size() }
        .maxOrNull() ?: 0
  }

  fun lookupFor(index: Int): Calculation.VariableLookup {
    return object : Calculation.VariableLookup {
      override fun <T : Any> get(variable: Input.Variable<T>): T? {
        @Suppress("UNCHECKED_CAST")
        val values = map[variable] as ValueContainer<T>
        return if (isSingleValue.contains(variable))
          values[0]
        else
          values[index]
      }
    }
  }

  companion object {
    fun variableMap(data: Data, variables: List<VariableMapping<out Any>>): VariableMap {
      return VariableMap(
          map = variables.map {
            require(it.columnConnection is ColumnConnection.ColumnValues) { "not implemented: ${it.columnConnection}" }
            require(it.variable is Input.Variable) { "not implemented: ${it.variable}" }
            it.variable to data[it.columnConnection.columnId]
          }.toMap(),
          isSingleValue = variables.mapNotNull {
            require(it.variable is Input.Variable) { "not implemented: ${it.variable}" }
            if (data.isSingleValue(it.columnConnection.columnId)) {
              it.variable
            } else null
          }.toSet()
      )
    }
  }
}