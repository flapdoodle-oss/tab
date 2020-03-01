package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.Calculation
import de.flapdoodle.tab.data.calculations.CalculationMapping
import de.flapdoodle.tab.data.calculations.VariableMap
import de.flapdoodle.tab.data.values.Variable
import javax.print.attribute.standard.Destination

sealed class ConnectableNode {
  abstract val id: NodeId<out ConnectableNode>
  abstract val name: String

  data class Table(
      override val name: String,
      override val id: NodeId.TableId = NodeId.TableId(),
      private val columns: List<NamedColumn<out Any>> = listOf()
  ) : ConnectableNode(), HasColumns {

    override fun columns() = columns

    fun add(id: ColumnId<*>, name: String): Table {
      require(!columns.any { it.id == id }) { "column already added" }

      return copy(columns = columns + NamedColumn(name,id))
    }
  }

  data class Calculated(
      override val name: String,
      override val id: NodeId.CalculatedId = NodeId.CalculatedId(),
      private val calculations: List<CalculationMapping<out Any>> = emptyList()
  ): ConnectableNode(), HasColumns, HasInputs, HasCalculations {

    override fun columns() = calculations.map(CalculationMapping<out Any>::column)
    override fun variables(): Set<Variable<out Any>> {
      return calculations
          .flatMap { it.calculation.variables() }
          .toSet()
    }

    override fun calculations() = calculations

    fun <T: Any> changeCalculation(destination: NamedColumn<T>, calculation: Calculation<T>): Calculated {
      return copy(calculations = calculations.map {
        if (it.column == destination) {
          (it as CalculationMapping<T>).copy(calculation = calculation)
        } else {
          it
        }
      })
    }

    fun calculate(data: Data, variableMap: VariableMap): Data {
      var currentData = data

//      val variableMap = VariableMap.variableMap(currentData, connections)

      calculations.forEach {
        val variables = it.calculation.variables()
        val size = variableMap.size(variables)
        (0..size).forEach { index ->
          val result = it.calculation.calculate(variableMap.lookupFor(index))
          currentData = currentData.change(it.column.id, index, result)
        }
      }
      return currentData
    }

  }
}