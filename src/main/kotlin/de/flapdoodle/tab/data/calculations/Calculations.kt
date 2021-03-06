package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.values.Input


// mehrere berechnungen pro zeile
// so kommen mehrere spalten raus..
sealed class Calculations<T : Any> : Calculation<T> {

  class Calc_1<A : Any, T : Any>(
      val a: Input.Variable<A>,
      val formula: (A?) -> T?
  ) : Calculations<T>() {
    override fun calculate(lookup: Calculation.VariableLookup): T? {
      return formula(lookup[a])
    }

    override fun variables() = setOf(a)
  }

  class Calc_2<A : Any, B : Any, T : Any>(
      val a: Input.Variable<A>,
      val b: Input.Variable<B>,
      val formula: (A?, B?) -> T?
  ) : Calculations<T>() {
    override fun calculate(lookup: Calculation.VariableLookup): T? {
      return formula(lookup[a], lookup[b])
    }

    override fun variables() = setOf(a, b)
  }
}