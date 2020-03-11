package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.nodes.Connections

data class PersistableConnections(
    val variableMappings: List<PersistentVariableMapping>
) {

  companion object : PersistableAdapter<Connections, PersistableConnections> {
    override fun toPersistable(source: Connections): PersistableConnections {
      return PersistableConnections(
          variableMappings = source.variableMappings.map(PersistentVariableMapping.Companion::toPersistable)
      )
    }

    override fun from(context: FromPersistableContext, source: PersistableConnections): Connections {
      return Connections(
        variableMappings = source.variableMappings.map {
          PersistentVariableMapping.from(context, it)
        }
      )
    }
  }
}