package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.fx.extensions.subscribeEvent
import de.flapdoodle.tab.graph.nodes.renderer.events.ConnectEvent
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point2D
import tornadofx.*

class ShowConnectHandleNode : Fragment() {
  private val start = SimpleObjectProperty(Point2D(0.0, 0.0))
  private val end = SimpleObjectProperty(Point2D(0.0, 0.0))
  private val connectionNode = ConnectionNode(start, end)

  override val root = group {
    this += connectionNode.apply {
      root.isVisible = false
    }

    subscribeEvent<ConnectEvent> {
      when (it.data) {
        is ConnectEvent.EventData.StartConnectTo<out Any> -> {
          start.value = it.data.toCoord
          end.value = it.data.toCoord
          connectionNode.root.isVisible = true
        }
        is ConnectEvent.EventData.StartConnectFrom<out Any> -> {
          start.value = it.data.fromCoord
          end.value = it.data.fromCoord
          connectionNode.root.isVisible = true
        }
        is ConnectEvent.EventData.ConntectTo<out Any> -> {
          start.value = it.data.fromCoord
        }
        is ConnectEvent.EventData.ConntectFrom<out Any> -> {
          end.value = it.data.toCoord
        }
        is ConnectEvent.EventData.StopConnect -> {
          connectionNode.root.isVisible = false
        }
      }
    }
  }
}