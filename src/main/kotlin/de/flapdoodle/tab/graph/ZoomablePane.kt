package de.flapdoodle.tab.graph

import de.flapdoodle.tab.graph.events.Marker
import de.flapdoodle.tab.graph.nodes.GraphNodeMoveHandler
import de.flapdoodle.tab.graph.nodes.GraphNodeResizeHandler
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Bounds
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*

class ZoomablePane : Fragment("My View") {
  private val scale = SimpleDoubleProperty(1.0)

  val content = pane {
    prefWidth = 100.0
    prefHeight = 100.0

    scaleXProperty().bind(scale)
    scaleYProperty().bind(scale)
  }

  override val root = pane {
    children += content

    val outputClip = Rectangle()
    clip = outputClip

    layoutBoundsProperty().addListener { ov: ObservableValue<out Bounds>?, oldValue: Bounds?, newValue: Bounds ->
      outputClip.width = newValue.width
      outputClip.height = newValue.height
    }

    style(append = true) {
      borderColor += box(
          top = Color.RED,
          right = Color.DARKGREEN,
          left = Color.ORANGE,
          bottom = Color.PURPLE
      )

      borderWidth += box(0.5.px)
    }

    Zoomable.enableZoom(this, scale)
    Zoomable.enableDrag(this, content)

    val resolver = GraphNodeMoveHandler.resolver
        .andThen(GraphNodeResizeHandler.resolver)

    Marker.addEventDelegate(this, scale, resolver)
  }
}
