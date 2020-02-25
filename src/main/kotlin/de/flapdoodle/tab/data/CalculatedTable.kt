package de.flapdoodle.tab.data

import de.flapdoodle.tab.data.calculations.Calculation
import de.flapdoodle.tab.data.values.Values
import de.flapdoodle.tab.data.values.Variable
import de.flapdoodle.tab.types.Id

data class CalculatedTable(
    private val id: Id<CalculatedTable> = Id.create(),
    private val variables: List<VariableMapping<out Any>> = emptyList(),
    private val calculations: List<CalculationMapping<out Any>> = emptyList()
) : HasColumns, HasInputs {

  override fun id() = id
  override fun columns() = calculations.map(CalculationMapping<out Any>::column)
  override fun variables(): Set<Variable<out Any>> {
    return calculations
        .flatMap { it.calculation.variables() }
        .toSet()
  }

  fun calculate(data: Data): Data {
    var currentData = data

    val variableMap = VariableMap.variableMap(currentData, variables)

    calculations.forEach {
      val variables = it.calculation.variables()
      val size = variableMap.size(variables)
      (0..size).forEach {index ->
        val result = it.calculation.calculate(variableMap.lookupFor(index))
        currentData = currentData.change(it.column.id, index, result)
      }
    }
    return currentData
  }

  data class VariableMapping<T: Any>(
      val columnId: ColumnId<T>,
      val variable: Variable<T>
  )

  data class CalculationMapping<T: Any>(
      val calculation: Calculation<T>,
      val column: NamedColumn<T>
  )

  data class VariableMap(
      val map: Map<Variable<out Any>, Values<Any>>
  ) {

    fun size(variables: Set<Variable<out Any>>): Int {
      require(map.keys.containsAll(variables)) { "not all variables are mapped: ${map.keys} < $variables"}
      return map.filterKeys { variables.contains(it) }
          .values.map { it.size() }
          .max() ?: 0
    }

    fun lookupFor(index: Int): Calculation.VariableLookup {
      return object : Calculation.VariableLookup {
        override fun <T : Any> get(variable: Variable<T>): T? {
          @Suppress("UNCHECKED_CAST")
          return (map[variable] as Values<T>)[index]
        }
      }
    }

    companion object {
      fun variableMap(data: Data, variables: List<VariableMapping<out Any>>): VariableMap {
        return VariableMap(variables.map {
          it.variable to data[it.columnId]
        }.toMap())
      }
    }
  }
}