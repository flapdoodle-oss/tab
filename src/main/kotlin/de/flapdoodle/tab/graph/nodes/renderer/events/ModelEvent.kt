package de.flapdoodle.tab.graph.nodes.renderer.events

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Model
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.Calculation
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Variable
import tornadofx.*
import java.math.BigDecimal

data class ModelEvent(
    val data: EventData
) : FXEvent() {

  sealed class EventData {
    fun asEvent(): ModelEvent {
      return ModelEvent(this)
    }

    abstract fun applyTo(model: Model): Model

    data class FormulaChanged<T : ConnectableNode>(
        val nodeId: NodeId<T>,
        val namedColumn: NamedColumn<BigDecimal>,
        val newCalculation: Calculation<BigDecimal>
    ) : EventData() {

      override fun applyTo(model: Model): Model {
        return model.changeNode(nodeId) {
          require(it is ConnectableNode.Calculated) {"not supported: $it"}
          it.changeCalculation(namedColumn, newCalculation)
        }
      }
    }

    data class Connect<T: Any>(
        val nodeId: NodeId<out ConnectableNode>,
        val variable: Variable<T>,
        val columnConnection: ColumnConnection<T>
    ) : EventData() {

      override fun applyTo(model: Model): Model {
        return model.connect(nodeId, variable, columnConnection)
      }
    }

    data class DataChanged<T: Any>(
        val id: ColumnId<out T>,
        val row: Int,
        val value: T?
    ) : EventData() {
      override fun applyTo(model: Model): Model {
        return model
      }
    }
  }
}

