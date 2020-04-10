package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.controls.layout.WeightGridPane
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.CalculationMapping
import de.flapdoodle.tab.data.calculations.EvalExCalculationAdapter
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasCalculations
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import de.flapdoodle.tab.lazy.LazyValue
import de.flapdoodle.tab.lazy.flatMapIndexedFrom
import de.flapdoodle.tab.lazy.map
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import tornadofx.*
import java.math.BigDecimal

class CalculationsXNode<T>(
    node: LazyValue<T>
) : Fragment()
    where T : HasCalculations,
          T : ConnectableNode {

  private val calculations = node.map {
    it.calculations()
  }

  override val root = WeightGridPane().apply {
    children.flatMapIndexedFrom(calculations) { index, mapping ->
      listOf(
          CalcNode(mapping, { node.value().id }) {
            fire(it)
          }.apply {
            WeightGridPane.setPosition(this,0,index)
          }
      )
    }
  }

  class CalcNode<T : Any>(
      val mapping: CalculationMapping<T>,
      nodeIdSupplier: () -> NodeId<out ConnectableNode>,
      onFormulaChanged: (ModelEvent) -> Unit
  ) : HBox() {
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
          //label("not supported: ${mapping.calculation::class}")
        }
      }

    }
  }

}