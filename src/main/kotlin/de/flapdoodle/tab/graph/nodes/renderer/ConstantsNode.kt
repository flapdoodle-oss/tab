package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.fx.layout.weightgrid.WeightGridPane
import de.flapdoodle.tab.converter.Converters
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.fx.extensions.fire
import de.flapdoodle.tab.graph.nodes.renderer.events.DataEvent
import de.flapdoodle.tab.graph.nodes.renderer.events.ModelEvent
import de.flapdoodle.tab.graph.nodes.renderer.modals.AddConstantModalView
import de.flapdoodle.fx.lazy.LazyValue
import de.flapdoodle.fx.lazy.bindFrom
import de.flapdoodle.fx.lazy.merge
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import tornadofx.*

class ConstantsNode(
    id: NodeId.ConstantsId,
    node: LazyValue<ConnectableNode.Constants>,
    data: LazyValue<Data>
) : Fragment() {
  private val values = node.merge(data) { n, d ->
    val row = d.values(n.columns().map { it.id })
    n.columns().map { columnValue(it, row) }
  }

  private fun <T : Any> columnValue(it: NamedColumn<T>, row: Data.Row): ColumnValue<T> {
    return ColumnValue(it, row[it.id])
  }

  override val root = vbox {
    this += WeightGridPane().apply {
      style {
        padding = box(4.0.px)
      }
      setColumnWeight(0, 1.0)
      setColumnWeight(1, 2.0)
      setColumnWeight(3, 1.0)

      children.bindFrom(values,
          keyOf = { it.column.id },
          extract = MappedNodes::nodes) { index, source, mapped ->
        mapped?.apply {
          update(index, source)
        } ?: MappedNodes.map(node, index, source)


      }

      children.addAll(
          MappedNodes.AddEntry(id, Int.MAX_VALUE).nodes()
      )
    }
  }

  sealed class MappedNodes() {
    abstract fun nodes(): List<Node>
    abstract fun update(index: Int, columnValue: ColumnValue<out Any>?)

    internal fun <T : Node> T.updateRow(row: Int) {
      WeightGridPane.updatePosition(this) { it.copy(row = row) }
    }

    class Entry<T : Any>(
        val nodeId: () -> NodeId.ConstantsId,
        val index: Int,
        columnValue: ColumnValue<T>
    ) : MappedNodes() {
      private var column = columnValue.column
      private val converter = Converters.converterFor(column.id.type)

      private val field = TextField(converter.toString(columnValue.value)).apply {
        action {
          println("")
          DataEvent.EventData.ValueChanged(
              id = column.id,
              value = converter.fromString(text)
          ).asEvent().fire()
        }
      }.withPosition(1, index)

      private val label = Label(column.name)
          .withPosition(0, index, horizontalPosition = HPos.LEFT)

//      private val button = Button("change").apply {
//        action {
//          println("should change formula to ${field.text}")
//          ModelEvent.EventData.FormulaChanged(
//              nodeId = nodeId(),
//              namedColumn = column,
//              newCalculation = EvalExCalculationAdapter(field.text)
//          ).asEvent().fire()
//        }
//      }.withPosition(2, index)

      private val deleteButton = Button("delete").apply {
        action {
          println("delete formula")
          ModelEvent.EventData.DeleteConstant(
              nodeId = nodeId(),
              columnId = column.id
          ).asEvent().fire()
        }
      }.withPosition(2, index)

      override fun nodes() = listOf(label, field, deleteButton)

      override fun update(index: Int, columnValue: ColumnValue<out Any>?) {
        require(columnValue != null) { "column is null" }

//        require(mapping != null && mapping.calculation is EvalExCalculationAdapter) { "update with wrong mapping: $mapping" }
//        calculation = mapping.calculation
        this.column = (columnValue as ColumnValue<T>).column

//        field.text = calculation.formula
        label.text = column.name

        nodes().forEach {
          it.updateRow(index)
        }
      }
    }

    class AddEntry(
        val nodeId: NodeId.ConstantsId,
        val index: Int
    ) : MappedNodes() {
      private val button = Button("add").apply {
        action {
          AddConstantModalView.openModalWith(nodeId)
        }
      }.withPosition(2, index)

      override fun nodes() = listOf(button)

      override fun update(index: Int, columnValue: ColumnValue<out Any>?) {
        button.updateRow(index)
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

      fun <V : Any> map(
          node: LazyValue<ConnectableNode.Constants>,
          index: Int,
          columnValue: ColumnValue<V>
      ): MappedNodes {
        return Entry({ node.value().id }, index, columnValue)
      }
    }
  }

  data class ColumnValue<T : Any>(val column: NamedColumn<T>, val value: T?)
}