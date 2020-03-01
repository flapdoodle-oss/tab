package de.flapdoodle.tab.graph.nodes.renderer

import com.sun.scenario.effect.Blend
import de.flapdoodle.tab.data.calculations.CalculationMapping
import de.flapdoodle.tab.data.nodes.ConnectableNode
import tornadofx.*

data class ModelEvent(
    val data: ModelEvent.EventData
) : FXEvent() {

  sealed class EventData {
    fun asEvent(): ModelEvent {
      return ModelEvent(this)
    }

    data class FormulaChanged<T : ConnectableNode>(
        val node: T,
        val calculation: CalculationMapping<out Any>,
        val changedFormula: String
    ) : EventData()
  }
}

