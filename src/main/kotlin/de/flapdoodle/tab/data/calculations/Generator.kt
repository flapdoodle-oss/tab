package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.values.Input
import de.flapdoodle.tab.types.Either

interface Generator<T: Any> {

  fun variables(): Set<Input.Variable<out Any>>
  fun generate(lookup: VariableLookup): List<T?>

  interface VariableLookup {
    operator fun <T: Any> get(variable: Input.Variable<T>): Either<T?, List<T?>>
  }
}