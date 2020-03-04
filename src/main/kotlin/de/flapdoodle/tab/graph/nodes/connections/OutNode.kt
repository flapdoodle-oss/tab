package de.flapdoodle.tab.graph.nodes.connections

import de.flapdoodle.tab.extensions.subscribeEvent
import de.flapdoodle.tab.graph.events.marker
import de.flapdoodle.tab.graph.nodes.renderer.events.ConnectEvent
import javafx.scene.Group
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.*

class OutNode(val out: Out, val color: Color = Color.ORANGE) : StackPane() {
  private val handle = circle {
    style {
      fill = color
    }
    radius = 4.0
    isMouseTransparent = true
  }

  init {
    apply {
      marker = out
      style {
        padding = box(2.0.px)
      }

      this += handle
    }
  }

  init {
    subscribeEvent<ConnectEvent> { event ->
      when (event.data) {
        is ConnectEvent.EventData.StartConnectTo<out Any> -> {
          if (this@OutNode.out is Out.ColumnValues<out Any> && this@OutNode.out.columnId.type==event.data.variable.type) {
            handle.style {
              fill = Color.BLUE
            }
            handle.radius = 8.0
          }
        }
        is ConnectEvent.EventData.StopConnect -> {
          handle.style {
            fill = color
          }
          handle.radius = 4.0
        }
      }
    }
  }

  fun centerX() = handle.layoutXProperty()
  fun centerY() = handle.layoutYProperty()
}