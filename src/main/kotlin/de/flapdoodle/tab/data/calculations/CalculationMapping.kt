package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.NamedColumn

data class CalculationMapping<T : Any>(
    val calculation: Calculation<T>,
    val column: NamedColumn<T>
) {

  fun calculate(data: Data, variableMap: VariableMap): Data {
    var currentData = data

    val variables = calculation.variables()
    if (variableMap.isValidFor(variables)) {
      val size = variableMap.size(variables)
      (0 until size).forEach { index ->
        val result = this.calculation.calculate(variableMap.lookupFor(index))
        currentData = currentData.change(this.column.id, index, result)
      }
    } else {
      currentData = currentData.clear(this.column.id)
    }

    return currentData
  }
}