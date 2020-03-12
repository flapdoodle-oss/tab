package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.values.Input

interface Calculation<T: Any> {

  fun variables(): Set<Input.Variable<out Any>>
  fun calculate(lookup: VariableLookup): T?

  interface VariableLookup {
    operator fun <T: Any> get(variable: Input.Variable<T>): T?
  }
}