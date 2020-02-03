package de.flapdoodle.tab.graph

import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*
import java.util.concurrent.atomic.AtomicReference

object MoveRect {

  fun enableMoveRect(root: Node) {
    val currentRect = AtomicReference<Rectangle>()
    val doMove = AtomicReference<MappedPoints>()

    root.addEventFilter(MouseEvent.MOUSE_CLICKED) { click ->
      val rect = currentRect.get()
      if (doMove.get() == null && rect != null) {
        doMove.set(MappedPoints(click.x, click.y, rect.x, rect.y))
        println(" -> " + doMove.get())

        click.consume()
      }
    }
    root.addEventFilter(MouseEvent.MOUSE_DRAGGED) { drag ->
      val rect = currentRect.get()
      if (rect!=null) {
        val start: MappedPoints? = doMove.get()
        if (start != null) {
          val newLocal: Point2D = start.scaledLocalCoord(Point2D(drag.x, drag.y), 1.0)
          println("drag to $newLocal")
          rect.relocate(newLocal.x, newLocal.y)
        }
        drag.consume()
      }
    }
    root.addEventFilter(MouseEvent.MOUSE_RELEASED) { release ->
      doMove.set(null)
    }

    root.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET) { event ->
      println("enter target ${event.target}")
      val target = event.target
      if (target is Rectangle) {
        currentRect.set(target)
        target.apply {
          style {
            fill = Color.RED
          }
        }
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET) { event ->
      println("leave target ${event.target}")
      currentRect.set(null)

      val target = event.target
      if (target is Rectangle) {
        target.apply {
          style {
            fill = Color.YELLOW
          }
        }
      }
    }

  }
}