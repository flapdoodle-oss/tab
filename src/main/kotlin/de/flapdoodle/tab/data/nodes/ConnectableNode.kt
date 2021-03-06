package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.Aggregation
import de.flapdoodle.tab.data.calculations.AggregationMapping
import de.flapdoodle.tab.data.calculations.Calculation
import de.flapdoodle.tab.data.calculations.CalculationMapping
import de.flapdoodle.tab.data.values.Input

// TODO Generator
sealed class ConnectableNode {
  abstract val id: NodeId<out ConnectableNode>
  abstract val name: String

  data class Constants(
      override val name: String,
      override val id: NodeId.ConstantsId = NodeId.ConstantsId(),
      private val columns: List<NamedColumn<out Any>> = listOf()
  ) : ConnectableNode(), HasColumns {

    override fun columns() = columns

    fun add(id: ColumnId<*>, name: String): Constants {
      require(!columns.any { it.id == id }) { "column already added" }

      return copy(columns = columns + NamedColumn(name, id))
    }

    fun remove(id: ColumnId<*>): Constants {
      require(columns.any { it.id == id }) { "column not found" }

      return copy(columns = columns.filter { it.id != id })
    }
  }

  data class Table(
      override val name: String,
      override val id: NodeId.TableId = NodeId.TableId(),
      private val columns: List<NamedColumn<out Any>> = listOf()
  ) : ConnectableNode(), HasColumns {

    override fun columns() = columns

    fun add(id: ColumnId<*>, name: String): Table {
      require(!columns.any { it.id == id }) { "column already added" }

      return copy(columns = columns + NamedColumn(name, id))
    }

    fun remove(id: ColumnId<*>): Table {
      require(columns.any { it.id == id }) { "column not found" }

      return copy(columns = columns.filter { it.id != id })
    }
  }

  data class Aggregated(
      override val name: String,
      override val id: NodeId.AggregatedId = NodeId.AggregatedId(),
      private val aggregations: List<AggregationMapping<out Any>> = emptyList()
  ) : ConnectableNode(), HasColumns, HasInputs {
    override fun columns() = aggregations.map(AggregationMapping<out Any>::column)
    override fun variables(): Set<Input<out Any>> {
      return aggregations
          .map { it.aggregation.variable() }
          .toSet()
    }

    fun aggregations() = aggregations

    fun <T : Any> add(column: NamedColumn<T>, aggregation: Aggregation<T>): Aggregated {
      return copy(aggregations = aggregations + AggregationMapping(aggregation, column))
    }
  }

  data class Calculated(
      override val name: String,
      override val id: NodeId.CalculatedId = NodeId.CalculatedId(),
      private val calculations: List<CalculationMapping<out Any>> = emptyList()
  ) : ConnectableNode(), HasColumns, HasInputs, HasCalculations {

    override fun columns() = calculations.map(CalculationMapping<out Any>::column)
    override fun variables(): Set<Input<out Any>> {
      return calculations
          .flatMap { it.calculation.variables() }
          .toSet()
    }

    override fun calculations() = calculations

    fun <T : Any> changeCalculation(destination: NamedColumn<T>, calculation: Calculation<T>): Calculated {
      return copy(calculations = calculations.map {
        if (it.column.id == destination.id) {
          (it as CalculationMapping<T>)
              .copy(
                  column = destination,
                  calculation = calculation
              )
        } else {
          it
        }
      })
    }

    fun <T : Any> add(column: NamedColumn<T>, calculation: Calculation<T>): Calculated {
      return copy(calculations = calculations + CalculationMapping(calculation, column))
    }

    fun <T : Any> delete(column: NamedColumn<T>): Calculated {
      return copy(calculations = calculations.filter { m -> m.column != column })
    }
  }
}