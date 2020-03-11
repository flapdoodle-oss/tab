package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.Nodes

data class PersistableNodes(
    private val nodes: List<PersistableConnectableNode>
) {

  companion object : PersistableAdapter<Nodes, PersistableNodes> {
    override fun toPersistable(source: Nodes): PersistableNodes {
      return PersistableNodes(source.nodes.map { entry ->
        PersistableConnectableNode.toPersistable(entry.value)
      })
    }

    override fun from(context: FromPersistableContext, source: PersistableNodes): Nodes {
      return Nodes(source.nodes.map {
        val node = PersistableConnectableNode.from(context,it)
        node.id to node
      }.toMap())
    }
  }
}