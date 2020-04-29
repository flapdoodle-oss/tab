package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.values.Input
import de.flapdoodle.tab.types.Either
import java.math.BigDecimal
import java.math.BigInteger

sealed class Generators<T : Any> : Generator<T> {
  data class Range(
      val startName: String,
      val stepName: String,
      val endName: String
  ) : Generators<BigDecimal>() {

    private val startVar = Input.Variable(BigDecimal::class, startName)
    private val stepVar = Input.Variable(BigDecimal::class, stepName)
    private val endVar = Input.Variable(BigDecimal::class, endName)

    override fun variables() = setOf(startVar, stepVar, endVar)

    override fun generate(lookup: Generator.VariableLookup): List<BigDecimal?> {
      val start = lookup[startVar]
      val step = lookup[stepVar]
      val end = lookup[endVar]

      require(start is Either.Left) { "invalid $startName" }
      require(step is Either.Left) { "invalid $stepName" }
      require(end is Either.Left) { "invalid $endName" }

      val s = start.value
      val inc = step.value
      val e = end.value

      if (s != null && inc != null && e != null) {
        val diff = e - s
        if (diff.signum() == inc.signum()) {
          val steps = (diff / inc).toBigInteger()
          // TODO data concept is maybe obsolete..
          if (steps < BigInteger.valueOf(1000L)) {
            val intSteps = steps.intValueExact()

            return (0..intSteps).map { s + (inc * BigDecimal.valueOf(it.toLong())) }
          }
        }
      }
      return emptyList<BigDecimal>()
    }
  }
}