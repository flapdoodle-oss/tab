package de.flapdoodle.tab.data.calculations

import com.udojava.evalex.Expression
import de.flapdoodle.tab.data.values.Input
import de.flapdoodle.tab.extensions.Exceptions
import java.math.BigDecimal
import java.math.RoundingMode

data class EvalExCalculationAdapter(
    val formula: String
) : Calculation<BigDecimal> {

  private val expression = Expression(formula)
  private val variables = expression.usedVariables.map { Input.Variable(BigDecimal::class, it) }.toSet()

  override fun variables() = variables
  override fun calculate(lookup: Calculation.VariableLookup): BigDecimal? {
    val variableMap = variables.map { it.name to lookup[it] }
    val maxScale = variableMap.mapNotNull { it.second }
        .map { it.scale() }
        .max() ?: 2

    return Exceptions.returnOnException<BigDecimal, ArithmeticException>(fallback = { ex ->
      ex.printStackTrace()
      null
    }) {

      return Expression(formula).apply {
            variableMap.forEach { (name, value) ->
              setVariable(name, value)
            }
          }
          .eval().let {
            it.setScale(maxScale, RoundingMode.HALF_DOWN)
          }
    }
  }
}