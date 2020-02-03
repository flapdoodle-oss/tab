package de.flapdoodle.tab.graph

import javafx.beans.property.DoubleProperty
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*
import java.util.concurrent.atomic.AtomicReference

object MoveRect {

  fun enableMoveRect(root: Node, scale: DoubleProperty) {
    var state: State = State.NotEntered

    root.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET) { event ->
//      println("enter target ${event.target}")
      val target = event.target
      if (target is Rectangle) {
        state = state.let {
          println("enter target: current state: $it")
          if (it is State.NotEntered) {
            event.consume()

            target.apply {
              style {
                fill = Color.RED
              }
            }

            it.enter(target)
          } else it
        }
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_PRESSED) { click ->
      state = state.let {
        println("mouse clicked: current state: $it")
        if (it is State.Entered) {
          click.consume()

          it.startMoving(MappedPoints(click.x, click.y, it.node.x, it.node.y))
        } else it
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_DRAGGED) { drag ->
      state.let {
        println("mouse dragged: current state: $it")
        if (it is State.StartMoving) {
          drag.consume()

          println("still moving")
          val newLocal: Point2D = it.mappedPoints.scaledLocalCoord(Point2D(drag.x, drag.y), scale.value)
          println("drag to $newLocal")
          //it.node.relocate(newLocal.x, newLocal.y)
          it.node.x = newLocal.x
          it.node.y = newLocal.y
        }
      }

    }
    root.addEventFilter(MouseEvent.MOUSE_RELEASED) { release ->
      state = state.let {
        println("mouse released: current state: $it")
        if (it is State.StartMoving) {
          release.consume()

          it.stopMoving()
        } else it
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET) { event ->
      state = state.let {
        println("exit target: current state: $it")
        if (it is State.Entered) {
          event.consume()

          it.node.apply {
            style {
              fill = Color.YELLOW
            }
          }
          it.leave()
        } else it
      }
    }

  }

  sealed class State {
    object NotEntered : State() {
      fun enter(target: Rectangle): Entered {
        println("enter -> $target")
        return Entered(target)
      }
    }

    data class Entered(val node: Rectangle) : State() {
      fun startMoving(mappedPoints: MappedPoints): State {
        println("start moving -> $node")
        return StartMoving(node, mappedPoints)
      }

      fun leave(): State {
        println("leave -> $node")
        return NotEntered
      }
    }

    data class StartMoving(val node: Rectangle, val mappedPoints: MappedPoints) : State() {
      fun stopMoving(): State {
        println("stopped moving -> $node")
        return Entered(node)
      }
    }
  }
}