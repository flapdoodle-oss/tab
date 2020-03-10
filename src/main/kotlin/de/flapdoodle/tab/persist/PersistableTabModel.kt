package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.TabModel

data class PersistableTabModel(
    val nodes: PersistableNodes,
    val nodeConnections: PersistableNodeConnections,
    val data: PersistableData
) {

  companion object : ToPersistable<TabModel, PersistableTabModel> {
    override fun toPersistable(source: TabModel): PersistableTabModel {
      return PersistableTabModel(
          nodes = PersistableNodes.toPersistable(source.nodes),
          nodeConnections = PersistableNodeConnections.toPersistable(source.nodeConnections),
          data = PersistableData.toPersistable(source.data)
      )
    }
  }
}