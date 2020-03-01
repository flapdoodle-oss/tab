package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.bindings.mapToList
import de.flapdoodle.tab.bindings.syncFrom
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.Calculation
import de.flapdoodle.tab.data.calculations.CalculationMapping
import de.flapdoodle.tab.data.calculations.EvalExCalculationAdapter
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasCalculations
import de.flapdoodle.tab.data.nodes.NodeId
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import tornadofx.*
import java.math.BigDecimal

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
      CalcNode(it!!, { node.value.id }) {
        fire(it)
      }
    }
  }

  class CalcNode<T : Any>(val mapping: CalculationMapping<T>, nodeIdSupplier: () -> NodeId<out ConnectableNode>, onFormulaChanged: (ModelEvent) -> Unit) : HBox() {
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
            onFormulaChanged(ModelEvent.EventData.FormulaChanged(
                nodeId = nodeIdSupplier(),
                namedColumn = mapping.column as NamedColumn<BigDecimal>,
                newCalculation = EvalExCalculationAdapter(field.text)
            ).asEvent())
          }
        }
        else -> {
          label("not supported: ${mapping.calculation::class}")
        }
      }

    }
  }

}