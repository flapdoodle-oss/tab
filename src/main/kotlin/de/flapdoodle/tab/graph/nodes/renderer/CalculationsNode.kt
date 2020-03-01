package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.bindings.mapToList
import de.flapdoodle.tab.bindings.syncFrom
import de.flapdoodle.tab.data.calculations.CalculationMapping
import de.flapdoodle.tab.data.calculations.EvalExCalculationAdapter
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasCalculations
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import tornadofx.*

class CalculationsNode<T>(
    node: ObservableValue<T>
) : Fragment()
    where T : HasCalculations,
          T : ConnectableNode {

  private val calculations = node.mapToList {
    it.calculations()
  }

  override val root = vbox {
    children.syncFrom(calculations) {
      CalcNode(it!!) { newFormula ->
        fire(ModelEvent.EventData.FormulaChanged(
            node.value, it!!, newFormula
        ).asEvent())
      }
    }
  }

  class CalcNode<T : Any>(val mapping: CalculationMapping<T>, onFormulaChanged: (String) -> Unit) : HBox() {
    init {
      alignment = Pos.CENTER_LEFT

      when (mapping.calculation) {
        is EvalExCalculationAdapter -> {
          val formula = mapping.calculation.formula
          label(mapping.column.name)
          val field = textfield(formula)
          button("change") {

          }.action {
            println("should change formula to ${field.text}")
            onFormulaChanged(field.text)
          }
        }
        else -> {
          label("not supported: ${mapping.calculation::class}")
        }
      }

    }
  }

}