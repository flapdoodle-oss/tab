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

  companion object : PersistableAdapter<NodeId<out ConnectableNode>, PersistableNodeId> {
    override fun toPersistable(source: NodeId<out ConnectableNode>): PersistableNodeId {
      return PersistableNodeId(
          id = source.id,
          type = TypeClassEnum.typeOf(source::class)
      )
    }

    override fun from(context: FromPersistableContext, source: PersistableNodeId): NodeId<out ConnectableNode> {
      return when (source.type) {
        Type.Table -> FromTableId.from(context,source)
        Type.Calculated -> FromCalculatedId.from(context,source)
      }
    }
  }

  object FromTableId : FromPersistable<NodeId.TableId, PersistableNodeId> {
    override fun from(context: FromPersistableContext, source: PersistableNodeId): NodeId.TableId {
      require(source.type==Type.Table) {"type mismatch: $source"}
      return context.tableIdFor(source.id)
    }
  }
  object FromCalculatedId : FromPersistable<NodeId.CalculatedId, PersistableNodeId> {
    override fun from(context: FromPersistableContext, source: PersistableNodeId): NodeId.CalculatedId {
      require(source.type==Type.Calculated) {"type mismatch: $source"}
      return context.calculatedIdFor(source.id)
    }
  }
}