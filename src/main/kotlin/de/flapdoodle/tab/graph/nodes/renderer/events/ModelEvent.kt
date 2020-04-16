package de.flapdoodle.tab.graph.nodes.renderer.events

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.TabModel
import de.flapdoodle.tab.data.calculations.Aggregation
import de.flapdoodle.tab.data.calculations.Calculation
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import de.flapdoodle.tab.data.values.Input
import de.flapdoodle.tab.graph.nodes.connections.In
import de.flapdoodle.tab.graph.nodes.connections.Out
import tornadofx.*
import java.math.BigDecimal
import kotlin.reflect.KClass

data class ModelEvent(
    val data: EventData
) : FXEvent() {

  companion object {
    fun <T : Any> connect(
        nodeId: NodeId<out ConnectableNode>,
        variable: Input<T>,
        columnConnection: ColumnConnection<T>): ModelEvent {
      return EventData.Connect(nodeId, variable, columnConnection).asEvent()
    }

    fun <T : Any> connect(
        input: In.Value<T>,
        marker: Out.ColumnValues<out Any>
    ): ModelEvent? {
      return if (input.variable.type == marker.columnId.type)
        @Suppress("UNCHECKED_CAST")
        connect(input.id, input.variable, ColumnConnection.ColumnValues(marker.columnId as ColumnId<T>))
      else
        null
    }

    fun <T : Any> connect(
        input: In.List<T>,
        marker: Out.Aggregate<out Any>
    ): ModelEvent? {
      return if (input.variable.type == marker.columnId.type)
        @Suppress("UNCHECKED_CAST")
        connect(input.id, input.variable, ColumnConnection.Aggregate(marker.columnId as ColumnId<T>))
      else
        null
    }

    fun <T : Any> deleteColumn(nodeId: NodeId.TableId, columnId: ColumnId<T>): ModelEvent {
      return EventData.DeleteColumn(nodeId, columnId).asEvent()
    }

    fun addNode(node: ConnectableNode): ModelEvent {
      return EventData.AddNode(node).asEvent()
    }

    fun deleteTable(nodeId: NodeId<*>): ModelEvent {
      return EventData.DeleteTable(nodeId).asEvent()
    }

    fun addColumn(nodeId: NodeId.TableId, name: String, type: KClass<out Any>): ModelEvent {
      return EventData.AddColumn(nodeId, name, type).asEvent()
    }

    fun <T : Any> addCalculation(nodeId: NodeId.CalculatedId, column: NamedColumn<T>, calculation: Calculation<T>): ModelEvent {
      return EventData.AddCalculation(nodeId, column, calculation).asEvent()
    }

    fun <T : Any> addAggregation(nodeId: NodeId.AggregatedId, column: NamedColumn<T>, aggregation: Aggregation<T>): ModelEvent {
      return EventData.AddAggregation(nodeId, column, aggregation).asEvent()
    }
  }

  sealed class EventData {
    fun asEvent(): ModelEvent {
      return ModelEvent(this)
    }

    abstract fun applyTo(model: TabModel): TabModel

    data class FormulaChanged<T : ConnectableNode>(
        val nodeId: NodeId<T>,
        val namedColumn: NamedColumn<BigDecimal>,
        val newCalculation: Calculation<BigDecimal>
    ) : EventData() {

      override fun applyTo(model: TabModel): TabModel {
        return model.applyNodeChanges { nodes ->
          nodes.changeNode(nodeId) {
            require(it is ConnectableNode.Calculated) { "not supported: $it" }
            it.changeCalculation(namedColumn, newCalculation)
          }
        }
      }
    }

    data class DeleteCalculation<T : ConnectableNode>(
        val nodeId: NodeId<T>,
        val namedColumn: NamedColumn<BigDecimal>
    ) : EventData() {

      override fun applyTo(model: TabModel): TabModel {
        return model.applyNodeChanges { nodes ->
          nodes.changeNode(nodeId) {
            require(it is ConnectableNode.Calculated) { "not supported: $it" }
            it.delete(namedColumn)
          }
        }
      }
    }

    data class AddColumn(
        val nodeId: NodeId.TableId,
        val name: String,
        val type: KClass<out Any>
    ) : EventData() {
      override fun applyTo(model: TabModel): TabModel {
        return model.applyNodeChanges { nodes ->
          nodes.changeNode(nodeId) { table ->
            table.add(ColumnId.create(type), name)
          }
        }
      }
    }

    data class AddCalculation<T : Any>(
        val nodeId: NodeId.CalculatedId,
        val column: NamedColumn<T>,
        val calculation: Calculation<T>
    ) : EventData() {
      override fun applyTo(model: TabModel): TabModel {
        return model.applyNodeChanges { nodes ->
          nodes.changeNode(nodeId) { table ->
            table.add(column, calculation)
          }
        }
      }
    }

    data class AddAggregation<T : Any>(
        val nodeId: NodeId.AggregatedId,
        val column: NamedColumn<T>,
        val aggregation: Aggregation<T>
    ) : EventData() {
      override fun applyTo(model: TabModel): TabModel {
        return model.applyNodeChanges { nodes ->
          nodes.changeNode(nodeId) { table ->
            table.add(column, aggregation)
          }
        }
      }
    }

    data class DeleteColumn<T : Any>(
        val nodeId: NodeId.TableId,
        val columnId: ColumnId<T>
    ) : EventData() {
      override fun applyTo(model: TabModel): TabModel {
        return model.applyNodeChanges { nodes ->
          nodes.changeNode(nodeId) {
            it.remove(columnId)
          }
        }
      }
    }

    data class AddNode(
        val table: ConnectableNode
    ) : EventData() {
      override fun applyTo(model: TabModel): TabModel {
        return model.applyNodeChanges { nodes ->
          nodes.add(table)
        }
      }
    }

    data class DeleteTable(
        val nodeId: NodeId<*>
    ) : EventData() {
      override fun applyTo(model: TabModel): TabModel {
        return model.applyNodeChanges { nodes ->
          nodes.delete(nodeId)
        }
      }
    }

    data class Connect<T : Any>(
        val nodeId: NodeId<out ConnectableNode>,
        val variable: Input<T>,
        val columnConnection: ColumnConnection<T>
    ) : EventData() {

      override fun applyTo(model: TabModel): TabModel {
        return model.connect(nodeId, variable, columnConnection)
      }
    }

  }
}

