package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.NamedColumn

data class AggregationMapping<T : Any>(
    val aggregation: Aggregation<T>,
    val column: NamedColumn<T>
) {

  fun aggregate(data: Data, variableMap: ListMap): Data {
    var currentData = data

    val variables = aggregation.variable()
    if (variableMap.isValidFor(variables)) {
      val result = this.aggregation.aggregate(variableMap.asLookup())
      currentData = currentData.change(this.column.id, result)
    } else {
      currentData = currentData.clear(this.column.id)
    }

    return currentData
  }

//  fun calculate(data: Data, variableMap: VariableMap): Data {
//    var currentData = data
//
//    val variables = calculation.variables()
//    if (variableMap.isValidFor(variables)) {
//      val size = variableMap.size(variables)
//      (0 until size).forEach { index ->
//        val result = this.calculation.calculate(variableMap.lookupFor(index))
//        currentData = currentData.change(this.column.id, index, result)
//      }
//    } else {
//      currentData = currentData.clear(this.column.id)
//    }
//
//    return currentData
//  }
}