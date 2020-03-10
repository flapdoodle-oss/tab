package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.nodes.ConnectableNode

data class PersistableConnectableNode(
    val name: String,
    val id: PersistableNodeId,
    val columns: List<PersistableNamedColumn> = emptyList(),
    val calculations: List<PersistableCalculationMapping> = emptyList()
) {

  companion object : ToPersistable<ConnectableNode, PersistableConnectableNode> {

    override fun toPersistable(source: ConnectableNode): PersistableConnectableNode {
      return when (source) {
        is ConnectableNode.Table -> PersistableConnectableNode(
            name = source.name,
            id = PersistableNodeId.toPersistable(source.id),
            columns = source.columns().map(PersistableNamedColumn.Companion::toPersistable)
        )
        is ConnectableNode.Calculated -> PersistableConnectableNode(
            name = source.name,
            id = PersistableNodeId.toPersistable(source.id),
            calculations = source.calculations().map(PersistableCalculationMapping.Companion::toPersistable)
        )
      }
    }
  }
}