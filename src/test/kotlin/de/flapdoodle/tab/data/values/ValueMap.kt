package de.flapdoodle.tab.data.values

import de.flapdoodle.tab.data.calculations.Calculation

data class ValueMap(
    private val map: Map<Variable<out Any>, Any> = emptyMap()
) : Calculation.VariableLookup {

  fun <T: Any>add(variable: Variable<T>, value: T): ValueMap {
    return copy(map = map + (variable to value))
  }

  inline fun <reified T: Any> add(name: String, value: T): ValueMap {
    return add(Variable(T::class, name), value)
  }

  override operator fun <T: Any> get(variable: Variable<T>): T? {
    @Suppress("UNCHECKED_CAST")
    return map[variable] as T?
  }
}