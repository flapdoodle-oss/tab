package de.flapdoodle.tab.graph

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.input.MouseDragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import tornadofx.*

class DragPlayground : Fragment() {
  private val content = pane {
    circle {
      radius = 20.0
      fill = Color.RED
      layoutX = 30.0
      layoutY = 30.0
    }

    rectangle {
      width = 20.0
      height = 20.0
      fill = Color.YELLOW
      layoutX = 70.0
      layoutY = 70.0
    }
  }

  init {
    content.addEventFilter(MouseEvent.ANY) {
//      println("-> $it")
      it.consume()
      if (it.eventType == MouseEvent.MOUSE_PRESSED) {
        it.isDragDetect = true
      }
      if (it.eventType == MouseEvent.DRAG_DETECTED) {
        val target = it.target
        if (target is Node) {
          println("start drag from $target")
          target.startFullDrag()
        }
      }
      if (it.eventType == MouseDragEvent.MOUSE_DRAG_OVER) {
        println("over: ${it.target}")
      }
      if (it.eventType == MouseDragEvent.MOUSE_DRAG_ENTERED) {
        println("enter: ${it.target}")
      }
      if (it.eventType == MouseDragEvent.MOUSE_DRAG_EXITED) {
        println("exit: ${it.target}")
      }
      if (it.eventType == MouseDragEvent.MOUSE_DRAG_RELEASED) {
        println("released: ${it.target}")
      }
    }
  }

  override val root = group {
    borderpane {
      prefWidth = 200.0
      prefHeight = 200.0

      style {
        backgroundColor = multi(Color.WHITE)
        backgroundRadius = multi(box(5.0.px))

        borderColor = multi(box(Color.BLUE))
        borderRadius += box(5.0.px, 5.0.px)
      }

      center {
        this+=content
      }
    }
  }
}