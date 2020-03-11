package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.NodePositions
import de.flapdoodle.tab.data.TabModel

data class PersistableTabModel(
    val nodes: PersistableNodes,
    val nodeConnections: PersistableNodeConnections,
    val data: PersistableData
) {

  companion object : PersistableAdapter<TabModel, PersistableTabModel> {
    override fun toPersistable(source: TabModel): PersistableTabModel {
      return PersistableTabModel(
          nodes = PersistableNodes.toPersistable(source.nodes),
          nodeConnections = PersistableNodeConnections.toPersistable(source.nodeConnections),
          data = PersistableData.toPersistable(source.data)
      )
    }

    override fun from(context: FromPersistableContext, source: PersistableTabModel): TabModel {
      return TabModel(
          nodes = PersistableNodes.from(context, source.nodes),
          nodeConnections = PersistableNodeConnections.from(context, source.nodeConnections),
          data = PersistableData.from(context, source.data)
      )
    }

  }
}