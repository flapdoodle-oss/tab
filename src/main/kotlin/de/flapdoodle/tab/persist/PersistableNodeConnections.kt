package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.NodeConnections

data class PersistableNodeConnections(
    val connections: List<PersistableNodeConnection>
) {

  companion object : PersistableAdapter<NodeConnections, PersistableNodeConnections> {
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

    override fun from(context: FromPersistableContext, source: PersistableNodeConnections): NodeConnections {
      return NodeConnections(
          connections = source.connections.map {
            val id = PersistableNodeId.from(context, it.id)
            val connections = PersistableConnections.from(context, it.connections)
            id to connections
          }.toMap()
      )
    }
  }

  data class PersistableNodeConnection(
      val id: PersistableNodeId,
      val connections: PersistableConnections
  )
}