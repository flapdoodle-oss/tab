package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

data class PersistableNodeId(
    val id: Int,
    val type: Type
) {

  enum class Type(
      override val type: KClass<out NodeId<out ConnectableNode>>
  ) : TypeClassEnum<Type, NodeId<out ConnectableNode>> {
    Table(NodeId.TableId::class),
    Calculated(NodeId.CalculatedId::class)
  }

  companion object : ToPersistable<NodeId<out ConnectableNode>, PersistableNodeId> {
    override fun toPersistable(source: NodeId<out ConnectableNode>): PersistableNodeId {
      return PersistableNodeId(
          id = source.id,
          type = TypeClassEnum.typeOf(source::class)
      )
    }
  }
}