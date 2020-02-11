package de.flapdoodle.tab.graph.events2

import de.flapdoodle.tab.extensions.change
import javafx.beans.property.DoubleProperty
import javafx.scene.Node
import javafx.scene.input.MouseEvent

object MouseEvents {

  fun addEventDelegate(root: Node, scale: DoubleProperty, handlerResolver: MouseEventHandlerResolver) {
    var state: State = State.NoHandler

    root.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET) {event ->
      state = state.change { state: State.WithHandler ->
        state.withHandler(state.handler.onEnter(event.target))
      }
      state = state.change { state: State.NoHandler ->
        state.withHandler(handlerResolver.onEnter(event.target))
      }
      event.consume()
    }

    root.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET) {event ->
      state = state.change {state: State.WithHandler->
        state.withHandler(state.handler.onExit(event.target)).apply {
          event.consume()
        }
      }
    }
  }

  sealed class State {
    fun withHandler(handler: MouseEventHandler?) = handler?.let { WithHandler(it) } ?: NoHandler

    object NoHandler : State()
    class WithHandler(val handler: MouseEventHandler) : State()
  }
}