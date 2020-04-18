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
import de.flapdoodle.tab.lazy.Position
import de.flapdoodle.tab.lazy.bindFrom
import de.flapdoodle.tab.lazy.map
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
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
        keyOf = { it.column.id },
        extract = MappedNodes::nodes) { entry, mapped ->
      when (entry) {
        is Position.IndexedEntry -> mapped?.apply {
          update(entry.index, entry.value)
        } ?: MappedNodes.map(node, entry.index, entry.value)
//        is Position.After -> mapped?.apply {
//          update(entry.index, null)
//        } ?: MappedNodes.AddEntry({ node.value().id }, entry.index)
        else -> MappedNodes.Unmapped()
      }
    }

    children.addAll(
        MappedNodes.AddEntry({ node.value().id }, Int.MAX_VALUE).nodes()
    )
  }
}

private fun <T : Node> T.updateRow(row: Int) {
  WeightGridPane.updatePosition(this) { it.copy(row = row) }
}

sealed class MappedNodes() {
  abstract fun nodes(): List<Node>
  abstract fun update(index: Int, mapping: CalculationMapping<out Any>?)

  class EvalNodes(
      val nodeId: () -> NodeId<out ConnectableNode>,
      val index: Int,
      calculation: EvalExCalculationAdapter,
      column: NamedColumn<BigDecimal>
  ) : MappedNodes() {
    private var calculation = calculation
    private var column = column

    private val field = TextField(calculation.formula)
        .withPosition(1, index)

    private val label = Label(column.name)
        .withPosition(0, index, horizontalPosition = HPos.LEFT)

    private val button = Button("change").apply {
      action {
        println("should change formula to ${field.text}")
        ModelEvent.EventData.FormulaChanged(
            nodeId = nodeId(),
            namedColumn = column,
            newCalculation = EvalExCalculationAdapter(field.text)
        ).asEvent().fire()
      }
    }.withPosition(2, index)

    private val deleteButton = Button("delete").apply {
      action {
        println("delete formula")
        ModelEvent.EventData.DeleteCalculation(
            nodeId = nodeId(),
            namedColumn = column
        ).asEvent().fire()
      }
    }.withPosition(3, index)

    override fun nodes() = listOf(label, field, button, deleteButton)

    override fun update(index: Int, mapping: CalculationMapping<out Any>?) {
      require(mapping != null && mapping.calculation is EvalExCalculationAdapter) { "update with wrong mapping: $mapping" }
      calculation = mapping.calculation
      column = mapping.column as NamedColumn<BigDecimal>

      field.text = calculation.formula
      label.text = column.name

      nodes().forEach {
        it.updateRow(index)
      }
    }
  }

  class AddEntry(
      val nodeId: () -> NodeId<out ConnectableNode>,
      val index: Int
  ) : MappedNodes() {
    private val button = Button("change").apply {
      action {
        println("add entry")
      }
    }.withPosition(2, index)

    override fun nodes() = listOf(button)

    override fun update(index: Int, mapping: CalculationMapping<out Any>?) {
      button.updateRow(index)
    }
  }

  class Unmapped() : MappedNodes() {
    override fun nodes(): List<Node> = emptyList()
    override fun update(index: Int, mapping: CalculationMapping<out Any>?) {
      println("can not updated unmapped: $mapping")
    }
  }

  companion object {
    private fun <T : Node> T.withPosition(
        column: Int,
        row: Int,
        horizontalPosition: HPos? = null,
        verticalPosition: VPos? = null
    ): T {
      WeightGridPane.setPosition(this, column, row, horizontalPosition, verticalPosition)
      return this
    }

    fun <T, V : Any> map(
        node: LazyValue<T>,
        index: Int,
        mapping: CalculationMapping<V>
    ): MappedNodes
        where T : HasCalculations,
              T : ConnectableNode {
      return when (mapping.calculation) {
        is EvalExCalculationAdapter -> EvalNodes({ node.value().id }, index, mapping.calculation, mapping.column as NamedColumn<BigDecimal>)
        else -> Unmapped()
      }
    }
  }
}