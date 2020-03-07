package de.flapdoodle.tab.graph.nodes.renderer.events

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Nodes
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.calculations.Calculation
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Variable
import de.flapdoodle.tab.graph.nodes.connections.Out
import de.flapdoodle.tab.graph.nodes.connections.VariableInput
import tornadofx.*
import java.math.BigDecimal
import kotlin.reflect.KClass

data class ModelEvent(
    val data: EventData
) : FXEvent() {

  companion object {
    fun <T : Any> connect(
        nodeId: NodeId<out ConnectableNode>,
        variable: Variable<T>,
        columnConnection: ColumnConnection<T>): ModelEvent {
      return EventData.Connect(nodeId, variable, columnConnection).asEvent()
    }

    fun <T : Any> connect(
        input: VariableInput<T>,
        marker: Out.ColumnValues<out Any>
    ): ModelEvent? {
      return if (input.variable.type == marker.columnId.type)
        @Suppress("UNCHECKED_CAST")
        connect(input.id, input.variable, ColumnConnection.ColumnValues(marker.columnId as ColumnId<T>))
      else
        null
    }

    fun <T: Any> deleteColumn(nodeId: NodeId.TableId, columnId: ColumnId<T>): ModelEvent {
      return EventData.DeleteColumn(nodeId, columnId).asEvent()
    }

    fun deleteTable(nodeId: NodeId<*>): ModelEvent {
      return EventData.DeleteTable(nodeId).asEvent()
    }

    fun addColumn(nodeId: NodeId.TableId, name: String, type: KClass<out Any>): ModelEvent {
      return EventData.AddColumn(nodeId,name,type).asEvent()
    }

    fun <T: Any> addCalculation(nodeId: NodeId.CalculatedId, column: NamedColumn<T>,calculation: Calculation<T>): ModelEvent {
      return EventData.AddCalculation(nodeId, column, calculation).asEvent()
    }
  }

  sealed class EventData {
    fun asEvent(): ModelEvent {
      return ModelEvent(this)
    }

    abstract fun applyTo(nodes: Nodes): Nodes

    data class FormulaChanged<T : ConnectableNode>(
        val nodeId: NodeId<T>,
        val namedColumn: NamedColumn<BigDecimal>,
        val newCalculation: Calculation<BigDecimal>
    ) : EventData() {

      override fun applyTo(nodes: Nodes): Nodes {
        return nodes.changeNode(nodeId) {
          require(it is ConnectableNode.Calculated) { "not supported: $it" }
          it.changeCalculation(namedColumn, newCalculation)
        }
      }
    }

    data class AddColumn(
        val nodeId: NodeId.TableId,
        val name: String,
        val type: KClass<out Any>
    ) : EventData() {
      override fun applyTo(nodes: Nodes): Nodes {
        return nodes.changeNode(nodeId) { table ->
          table.add(ColumnId.create(type), name)
        }
      }
    }

    data class AddCalculation<T: Any>(
        val nodeId: NodeId.CalculatedId,
        val column: NamedColumn<T>,
        val calculation: Calculation<T>
    ) : EventData() {
      override fun applyTo(nodes: Nodes): Nodes {
        return nodes.changeNode(nodeId) { table ->
          table.add(column, calculation)
        }
      }
    }

    data class DeleteColumn<T: Any>(
        val nodeId: NodeId.TableId,
        val columnId: ColumnId<T>
    ): EventData() {
      override fun applyTo(nodes: Nodes): Nodes {
        return nodes.changeNode(nodeId) {
          it.remove(columnId)
        }
      }
    }

    data class DeleteTable(
        val nodeId: NodeId<*>
    ): EventData() {
      override fun applyTo(nodes: Nodes): Nodes {
        return nodes.delete(nodeId)
      }
    }

    data class Connect<T : Any>(
        val nodeId: NodeId<out ConnectableNode>,
        val variable: Variable<T>,
        val columnConnection: ColumnConnection<T>
    ) : EventData() {

      override fun applyTo(nodes: Nodes): Nodes {
        return nodes.connect(nodeId, variable, columnConnection)
      }
    }

  }
}

