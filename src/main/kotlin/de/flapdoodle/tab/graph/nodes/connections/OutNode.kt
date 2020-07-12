package de.flapdoodle.tab.graph.nodes.connections

import de.flapdoodle.fx.extensions.subscribeEvent
import de.flapdoodle.tab.graph.events.marker
import de.flapdoodle.tab.graph.nodes.renderer.events.ConnectEvent
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import tornadofx.*

class OutNode(
    val out: Out<out Any>,
    val color: Color = Color.ORANGE
) : Control() {

  private val skin = Skin(this)

  override fun createDefaultSkin() = skin

  class Skin(
      val control: OutNode
  ) : SkinBase<OutNode>(control) {

    private val handle = Circle().apply {
      style {
        fill = control.color
      }
      radius = 4.0
      isMouseTransparent = true
    }

    init {
      control.apply {
        marker = out
        style {
//          padding = box(2.0.px)
        }

        this += handle
      }
    }

    init {
      control.subscribeEvent<ConnectEvent> { event ->
        when (event.data) {
          is ConnectEvent.EventData.StartConnectTo<out Any> -> {
            if (control.out is Out.ColumnValues<out Any> && control.out.columnId.type == event.data.variable.type) {
              handle.style {
                fill = Color.BLUE
              }
              handle.radius = 8.0
            }
          }
          is ConnectEvent.EventData.StopConnect -> {
            handle.style {
              fill = control.color
            }
            handle.radius = 4.0
          }
        }
      }
    }
  }
}