package de.flapdoodle.tab.graph

import javafx.beans.property.DoubleProperty
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import java.util.concurrent.atomic.AtomicReference

object Zoomable {
  private const val MIN = 0.1
  private const val MAX = 10.0
  private const val ZOOM_SPEED = 1.2

  fun enableZoom(node: Node, scale: DoubleProperty) {
    node.addEventFilter(ScrollEvent.ANY) { event: ScrollEvent ->
      val delta = event.deltaY
      if (delta != 0.0) {
        var current = scale.get()

        current = if (delta < 0) {
          current / ZOOM_SPEED
        } else {
          current * ZOOM_SPEED
        }
        current = clamp(current)

        scale.set(current)
      }
    }
  }

  fun enableDrag(root: Node, content: Node) {
    val doDrag: AtomicReference<ScaledPoints?> = AtomicReference<ScaledPoints?>()

    root.setOnMousePressed(EventHandler { click: MouseEvent ->
      if (doDrag.get() == null) {
        doDrag.set(ScaledPoints(click.x, click.y, content.getLayoutX(), content.getLayoutY()))
        println(" -> " + doDrag.get())
      }
      click.consume()
    })

    root.setOnMouseDragged(EventHandler { drag: MouseEvent ->
      val start: ScaledPoints? = doDrag.get()
      if (start != null) {
        val newLocal: Point2D = start.scaledLocalCoord(Point2D(drag.x, drag.y), 1.0)
        println("drag to $newLocal")
        content.relocate(newLocal.x, newLocal.y)
      }
      drag.consume()
    })

    root.addEventFilter<MouseEvent>(MouseEvent.MOUSE_RELEASED, EventHandler { exit: MouseEvent? ->
      println("drag stopp....")
      // root.relocate(10 + root.getLayoutX(), 10 + root.getLayoutY());
      doDrag.set(null)
    })
  }

  private fun clamp(value: Double): Double {
    if (value < MIN) {
      return MIN
    }
    return if (value > MAX) {
      MAX
    } else value
  }
}