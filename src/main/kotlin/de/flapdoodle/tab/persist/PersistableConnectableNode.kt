package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.nodes.ConnectableNode

data class PersistableConnectableNode(
    val name: String,
    val type: Type,
    val id: PersistableNodeId,
    val columns: List<PersistableNamedColumn> = emptyList(),
    val calculations: List<PersistableCalculationMapping> = emptyList()
) {

  enum class Type {
    Table,
    Calculated
  }

  companion object : PersistableAdapter<ConnectableNode, PersistableConnectableNode> {

    override fun toPersistable(source: ConnectableNode): PersistableConnectableNode {
      return when (source) {
        is ConnectableNode.Table -> PersistableConnectableNode(
            name = source.name,
            type = Type.Table,
            id = PersistableNodeId.toPersistable(source.id),
            columns = source.columns().map(PersistableNamedColumn.Companion::toPersistable)
        )
        is ConnectableNode.Calculated -> PersistableConnectableNode(
            name = source.name,
            type = Type.Calculated,
            id = PersistableNodeId.toPersistable(source.id),
            calculations = source.calculations().map(PersistableCalculationMapping.Companion::toPersistable)
        )
      }
    }

    override fun from(context: FromPersistableContext, source: PersistableConnectableNode): ConnectableNode {
      return when (source.type) {
        Type.Table -> ConnectableNode.Table(
            name = source.name,
            id = PersistableNodeId.FromTableId.from(context, source.id),
            columns = source.columns.map { PersistableNamedColumn.from(context, it) }
        )
        Type.Calculated -> ConnectableNode.Calculated(
            name = source.name,
            id = PersistableNodeId.FromCalculatedId.from(context, source.id),
            calculations = source.calculations.map { PersistableCalculationMapping.from(context, it) }
        )
      }
    }
  }
}