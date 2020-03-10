package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.NodeConnections

data class PersistableNodeConnections(
    val connections: List<PersistableNodeConnection>
) {

  companion object : ToPersistable<NodeConnections, PersistableNodeConnections> {
    override fun toPersistable(source: NodeConnections): PersistableNodeConnections {
      return PersistableNodeConnections(
          connections = source.connections.map { entry ->
            PersistableNodeConnection(
                id = PersistableNodeId.toPersistable(entry.key),
                connections = PersistableConnections.toPersistable(entry.value)
            )
          }
      )
    }
  }

  data class PersistableNodeConnection(
      val id: PersistableNodeId,
      val connections: PersistableConnections
  )
}