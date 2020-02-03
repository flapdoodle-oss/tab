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
    var mouseState: MouseState = MouseState.Hoover
//    var nodeState: NodeState = NodeState.NotEntered
    var eventTargetStack = EventTargetStack()


    root.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET) { event ->
      debug("enter -> $eventTargetStack -> ${event.target}")

      eventTargetStack = eventTargetStack.enter(event.target)

//      nodeState = nodeState.change { state: NodeState.Entered ->
//        event.consume()
//
//        state.enter(event.target)
//      }
//      nodeState = nodeState.change { state: NodeState.NotEntered ->
//        event.consume()
//
//        state.enter(event.target)
//      }
    }

    root.addEventFilter(MouseEvent.MOUSE_PRESSED) { click ->
      debug("mouse pressed -> $eventTargetStack, mouse: $mouseState")

      val target = eventTargetStack.top()
      if (target!=null) {
        val listener = listenerLookup.listenerFor(target)
        if (listener != null) {
          mouseState = mouseState.change { it: MouseState.Hoover ->
            click.consume()

            it.startMoving(listener, Point2D(click.x, click.y))
          }
        }
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_DRAGGED) { drag ->
      debug("mouse dragged -> $eventTargetStack, mouse: $mouseState")

      mouseState.matches { state: MouseState.Moving ->
        drag.consume()
        val target = eventTargetStack.top()

        val newLocal: Point2D = state.start.scaledChange(Point2D(drag.x, drag.y), scale.value)
        state.listener.drag(newLocal.x, newLocal.y, target)
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_RELEASED) { release ->
      debug("mouse released -> $eventTargetStack, mouse: $mouseState")

      mouseState = mouseState.changeState<MouseState.Moving> {
        release.consume()

        it.listener.done()
        it.stopMoving()
      }
    }

    root.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET) { event ->
      debug("exit -> $eventTargetStack -> ${event.target}")

      eventTargetStack = eventTargetStack.leave(event.target)

//      nodeState = nodeState.change { state: NodeState.Entered ->
//        event.consume()
//
//        state.leave(event.target)
//      }
    }

  }

  private fun debug(msg: String) {
    //println(msg)
  }

//  sealed class NodeState {
//    object NotEntered : NodeState() {
//      fun enter(eventTarget: EventTarget): Entered {
//        debug("> enter $eventTarget")
//        return Entered(listOf(eventTarget))
//      }
//    }
//
//    data class Entered(val eventTargets: List<EventTarget>) : NodeState() {
//      fun enter(eventTarget: EventTarget): Entered {
//        debug(">> enter $eventTarget")
//        require(!eventTargets.contains(eventTarget)) { "already entered into $eventTarget" }
//        return Entered(eventTargets + eventTarget)
//      }
//
//      fun leave(eventTarget: EventTarget): NodeState {
//        debug("> leave $eventTarget")
//        require(eventTargets.contains(eventTarget)) { "target not found $eventTarget" }
//        val targets = eventTargets - eventTarget
//        return if (targets.isEmpty())
//          NotEntered
//        else
//          Entered(targets)
//      }
//
//      fun eventTarget() = eventTargets.last()
//    }
//  }

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
}