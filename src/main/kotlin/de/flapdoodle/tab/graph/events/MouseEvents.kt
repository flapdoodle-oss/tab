package de.flapdoodle.tab.graph.events

import de.flapdoodle.tab.extensions.change
import de.flapdoodle.tab.extensions.matches
import de.flapdoodle.tab.extensions.scaledChange
import javafx.beans.property.DoubleProperty
import javafx.event.EventTarget
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent

object MouseEvents {

  fun addEventDelegate(root: Node, scale: DoubleProperty, listenerLookup: MouseDragListenerLookup) {
//    var state: State = State.NotEntered
    var mouseState: MouseState = MouseState.Hoover
    var nodeState: NodeState = NodeState.NotEntered


    root.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET) { event ->
      //      println("enter target ${event.target}")
      nodeState = nodeState.change { state: NodeState.NotEntered ->
        event.consume()
        state.enter(event.target)
      }
//
//      val listener = listenerLookup.listenerFor(event.target)
//
//      if (listener != null) {
//        state = state.let {
//          println("enter target: current state: $it")
//          if (it is State.NotEntered) {
//            event.consume()
//            it.enter(listener)
//          } else it
//        }
//      }
    }

    root.addEventFilter(MouseEvent.MOUSE_PRESSED) { click ->
      nodeState.matches {state: NodeState.Entered ->
        val listener = listenerLookup.listenerFor(state.eventTarget)
        if (listener!=null) {
          mouseState = mouseState.change { it: MouseState.Hoover ->
            click.consume()
            it.startMoving(listener, Point2D(click.x, click.y))
          }
        }
      }

//      state = state.let {
//        println("mouse clicked: current state: $it")
//        if (it is State.Entered) {
//          click.consume()
//
//          it.startMoving(ScaledPoints(click.x, click.y))
//        } else it
//      }
    }

    root.addEventFilter(MouseEvent.MOUSE_DRAGGED) { drag ->
      mouseState.matches {state: MouseState.Moving ->
        drag.consume()

        val newLocal: Point2D = state.start.scaledChange(Point2D(drag.x, drag.y), scale.value)
        state.listener.drag(newLocal.x, newLocal.y, drag.target)
      }

//      state.let {
//        println("mouse dragged: current state: $it")
//        if (it is State.StartMoving) {
//          drag.consume()
//
//          println("still moving")
//          val newLocal: Point2D = it.points.scaledCoord(Point2D(drag.x, drag.y), scale.value)
//          println("drag to $newLocal")
//          it.listener.drag(newLocal.x, newLocal.y)
//        }
//      }

    }
    root.addEventFilter(MouseEvent.MOUSE_RELEASED) { release ->
      mouseState = mouseState.changeState<MouseState.Moving> { it.stopMoving() }
//      state = state.let {
//        println("mouse released: current state: $it")
//        if (it is State.StartMoving) {
//          release.consume()
//
//          it.stopMoving()
//        } else it
//      }
    }

    root.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET) { event ->
      nodeState = nodeState.change { state: NodeState.Entered -> state.leave() }

//      state = state.let {
//        println("exit target: current state: $it")
//        if (it is State.Entered) {
//          event.consume()
//
//          it.leave()
//        } else it
//      }
    }

  }

  sealed class NodeState {
    object NotEntered : NodeState() {
      fun enter(eventTarget: EventTarget) = Entered(eventTarget)
    }

    data class Entered(val eventTarget: EventTarget) : NodeState() {
      fun leave() = NotEntered
    }
  }

  sealed class MouseState() {
    inline fun <reified T : MouseState> changeState(action: (T) -> MouseState): MouseState {
      return if (this is T) {
        action(this)
      } else this
    }

    inline fun <reified T : MouseState> onState(action: (T) -> Unit) {
      if (this is T) action(this)
    }

    object Hoover : MouseState() {
      fun startMoving(listener: MouseDragListener, start: Point2D) = Moving(listener, start)
    }

    data class Moving(val listener: MouseDragListener, val start: Point2D) : MouseState() {
      fun stopMoving() = Hoover
    }
  }

//  sealed class State {
//    object NotEntered : State() {
//      fun enter(listener: MouseDragListener): Entered {
//        println("enter -> $listener")
//        return Entered(listener)
//      }
//    }
//
//    data class Entered(val listener: MouseDragListener) : State() {
//      fun startMoving(points: ScaledPoints): State {
//        println("start moving -> $listener")
//        return StartMoving(listener, points)
//      }
//
//      fun leave(): State {
//        println("leave -> $listener")
//        return NotEntered
//      }
//    }
//
//    data class StartMoving(val listener: MouseDragListener, val points: ScaledPoints) : State() {
//      fun stopMoving(): State {
//        println("stopped moving -> $listener")
//        return Entered(listener)
//      }
//    }
//  }
}