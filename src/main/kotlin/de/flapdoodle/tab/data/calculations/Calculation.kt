package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.values.Variable

interface Calculation<T: Any> {

  fun variables(): Set<Variable<out Any>>
  fun calculate(lookup: VariableLookup): T?

  interface VariableLookup {
    operator fun <T: Any> get(variable: Variable<T>): T?
  }
}