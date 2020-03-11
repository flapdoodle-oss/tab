package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.NodePositions
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D

data class PersistableNodePositions(
    val positions: List<Entry>
) {
  companion object : PersistableAdapter<NodePositions, PersistableNodePositions> {
    override fun toPersistable(source: NodePositions): PersistableNodePositions {
      return PersistableNodePositions(
          positions = source.positions.map {
            Entry(
                nodeId = PersistableNodeId.toPersistable(it.key),
                x = it.value.first.x,
                y = it.value.first.y,
                width = it.value.second.width,
                height = it.value.second.height
            )
          }
      )
    }

    override fun from(context: FromPersistableContext, source: PersistableNodePositions): NodePositions {
      return NodePositions(
          positions = source.positions.map {
            val nodeId = PersistableNodeId.from(context, it.nodeId)
            val position = Point2D(it.x, it.y)
            val size = Dimension2D(it.width, it.height)
            nodeId to (position to size)
          }.toMap()
      )
    }
  }

  data class Entry(
      val nodeId: PersistableNodeId,
      val x: Double,
      val y: Double,
      val width: Double,
      val height: Double
  )
}