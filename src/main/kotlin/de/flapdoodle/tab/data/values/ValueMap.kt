package de.flapdoodle.tab.data.values

import de.flapdoodle.tab.data.calculations.Calculation

data class ValueMap(
    private val map: Map<Variable<out Any>, Any> = emptyMap()
) : Calculation.VariableLookup {

  override operator fun <T: Any> get(variable: Variable<T>): T? {
    @Suppress("UNCHECKED_CAST")
    return map[variable] as T?
  }
}