package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.nodes.NodeId
import kotlin.reflect.KClass

class FromPersistableContext {
  private var tableIds: Map<Int, NodeId.TableId> = emptyMap()
  private var calculatedIds: Map<Int, NodeId.CalculatedId> = emptyMap()
  private var aggregatedIds: Map<Int, NodeId.AggregatedId> = emptyMap()
  private var columnIds: Map<Pair<Int,KClass<out Any>>, ColumnId<out Any>> = emptyMap()

  fun tableIdFor(id: Int): NodeId.TableId {
    val ret = tableIds[id]
    return if (ret == null) {
      val newId = NodeId.TableId()
      tableIds = tableIds + (id to newId)
      newId
    } else ret
  }

  fun calculatedIdFor(id: Int): NodeId.CalculatedId {
    val ret = calculatedIds[id]
    return if (ret == null) {
      val newId = NodeId.CalculatedId()
      calculatedIds = calculatedIds + (id to newId)
      newId
    } else ret
  }

  fun aggregatedIdFor(id: Int): NodeId.AggregatedId {
    val ret = aggregatedIds[id]
    return if (ret == null) {
      val newId = NodeId.AggregatedId()
      aggregatedIds = aggregatedIds + (id to newId)
      newId
    } else ret
  }

  fun <T: Any> columnIdFor(id: Int, type: KClass<T>): ColumnId<T> {
    @Suppress("UNCHECKED_CAST")
    val ret = columnIds[id to type] as ColumnId<T>?
    return if (ret == null) {
      val newId = ColumnId.create(type)
      columnIds = columnIds + ((id to type) to newId)
      newId
    } else ret
  }
}