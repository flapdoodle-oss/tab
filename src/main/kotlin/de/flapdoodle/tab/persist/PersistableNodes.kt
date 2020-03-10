package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.Nodes

data class PersistableNodes(
    private val nodes: List<PersistableConnectableNode>
) {

  companion object : ToPersistable<Nodes, PersistableNodes> {
    override fun toPersistable(source: Nodes): PersistableNodes {
      return PersistableNodes(source.nodes.map { entry ->
        PersistableConnectableNode.toPersistable(entry.value)
      })
    }
  }
}