package de.flapdoodle.tab.data.nodes

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

sealed class NodeId<T : ConnectableNode> {
  internal abstract val id: Int

  data class TableId(
      override val id: Int = nextIdFor(ConnectableNode.Table::class)
  ) : NodeId<ConnectableNode.Table>()

  data class AggregatedId(
      override val id: Int = nextIdFor(ConnectableNode.Aggregated::class)
  ) : NodeId<ConnectableNode.Aggregated>()

  data class CalculatedId(
      override val id: Int = nextIdFor(ConnectableNode.Calculated::class)
  ) : NodeId<ConnectableNode.Calculated>()

  companion object {
    private val idGeneratorMap = ConcurrentHashMap<KClass<out Any>, AtomicInteger>()

    private fun nextIdFor(type: KClass<out Any>): Int {
      return idGeneratorMap.getOrPut(type, { AtomicInteger() }).incrementAndGet()
    }
  }
}