package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.controls.layout.weightgrid.WeightGridPane
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.CalculationMapping
import de.flapdoodle.tab.data.calculations.EvalExCalculationAdapter
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasCalculations
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.extensions.fire
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import de.flapdoodle.tab.lazy.LazyValue
import de.flapdoodle.tab.lazy.bindFrom
import de.flapdoodle.tab.lazy.flatMapIndexedFrom
import de.flapdoodle.tab.lazy.map
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import tornadofx.*
import java.math.BigDecimal

class CalculationsNode<T>(
    node: LazyValue<T>
) : Fragment()
    where T : HasCalculations,
          T : ConnectableNode {

  private val calculations = node.map {
    it.calculations()
  }

  override val root = WeightGridPane().apply {
    style {
      padding = box(4.0.px)
    }
    setColumnWeight(0, 1.0)
    setColumnWeight(1, 2.0)
    setColumnWeight(3, 1.0)

    children.bindFrom(calculations,
        reIndex = { index, _, nodes ->
          nodes.forEach {
            it.updateRow(index)
          }
        })
    { index, mapping ->
      when (mapping.calculation) {
        is EvalExCalculationAdapter -> {
          val field = TextField(mapping.calculation.formula)
              .withPosition(1, index)

          listOf(
              Label(mapping.column.name)
                  .withPosition(0, index, horizontalPosition = HPos.LEFT),
              field,
              Button("change").apply {
                action {
                  println("should change formula to ${field.text}")
                  ModelEvent.EventData.FormulaChanged(
                      nodeId = node.value().id,
                      namedColumn = mapping.column as NamedColumn<BigDecimal>,
                      newCalculation = EvalExCalculationAdapter(field.text)
                  ).asEvent().fire()
                }
              }.withPosition(2, index)
          )
        }
        else -> emptyList()
      }
    }
  }

  private fun <T : Node> T.withPosition(
      column: Int,
      row: Int,
      horizontalPosition: HPos? = null,
      verticalPosition: VPos? = null
  ): T {
    WeightGridPane.setPosition(this, column, row, horizontalPosition, verticalPosition)
    return this
  }

  private fun <T : Node> T.updateRow(row: Int) {
    WeightGridPane.updatePosition(this) { it.copy(row = row) }
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