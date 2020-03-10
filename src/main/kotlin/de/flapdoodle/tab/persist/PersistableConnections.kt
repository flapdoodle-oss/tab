package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.nodes.Connections

data class PersistableConnections(
    val variableMappings: List<PersistentVariableMapping>
) {

  companion object : ToPersistable<Connections, PersistableConnections> {
    override fun toPersistable(source: Connections): PersistableConnections {
      return PersistableConnections(
          variableMappings = source.variableMappings.map(PersistentVariableMapping.Companion::toPersistable)
      )
    }
  }
}