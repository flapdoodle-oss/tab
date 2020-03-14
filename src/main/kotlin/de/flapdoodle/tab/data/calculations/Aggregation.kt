package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.values.Input

interface Aggregation<T : Any> {
  fun variable(): Input.List<out Any>
  fun aggregate(lookup: VariableLookup): T?

  interface VariableLookup {
    operator fun <T: Any> get(variable: Input.List<T>): List<T?>?
  }
}