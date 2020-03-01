package de.flapdoodle.tab.graph.nodes.renderer

import com.sun.scenario.effect.Blend
import de.flapdoodle.tab.data.Model
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.Calculation
import de.flapdoodle.tab.data.calculations.CalculationMapping
import de.flapdoodle.tab.data.calculations.EvalExCalculationAdapter
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
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
  }
}

