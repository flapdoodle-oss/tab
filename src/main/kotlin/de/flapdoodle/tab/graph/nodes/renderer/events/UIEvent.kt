package de.flapdoodle.tab.graph.nodes.renderer.events

import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.NodeId
import javafx.geometry.Bounds
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import tornadofx.*

data class UIEvent(
  val eventData: EventData
) : FXEvent() {

  sealed class EventData {
    data class  NodeMoved(val id: NodeId<out ConnectableNode>, val position: Point2D, val size: Dimension2D) : EventData()
    data class  MoveNode(val id: NodeId<out ConnectableNode>, val position: Point2D, val size: Dimension2D) : EventData()
  }

  companion object {
    fun nodeMoved(id: NodeId<out ConnectableNode>, position: Point2D, size: Dimension2D): UIEvent {
      return UIEvent(EventData.NodeMoved(id, position, size))
    }

    fun moveNode(id: NodeId<out ConnectableNode>, position: Point2D, size: Dimension2D): UIEvent {
      return UIEvent(EventData.MoveNode(id, position, size))
    }
  }
}