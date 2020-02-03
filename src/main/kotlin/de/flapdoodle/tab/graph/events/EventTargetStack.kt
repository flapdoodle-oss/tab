package de.flapdoodle.tab.graph.events

import javafx.event.EventTarget

data class EventTargetStack(private val stack: List<EventTarget> = emptyList()) {

  fun enter(eventTarget: EventTarget): EventTargetStack {
    require(!stack.contains(eventTarget)) {"target already found in stack: $eventTarget"}
    return copy(stack = stack + eventTarget)
  }

  fun leave(eventTarget: EventTarget): EventTargetStack {
    require(stack.contains(eventTarget)) {"target NOT found in stack: $eventTarget"}
    return copy(stack = stack - eventTarget)
  }

  fun top() = stack.lastOrNull()
}