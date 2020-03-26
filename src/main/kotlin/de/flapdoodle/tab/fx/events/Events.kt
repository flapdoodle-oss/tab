package de.flapdoodle.tab.fx.events

import javafx.event.Event
import javafx.event.EventDispatcher
import javafx.event.EventTarget
import javafx.event.EventType
import javafx.scene.Node
import javafx.scene.Parent

fun Node.fireEventToChildren(event: Event, filter: (Node) -> Events.Result = { Events.Result.Continue }) {
  Event.fireEvent(Events.eventTargetForChildren(this, filter), event)
}

fun <T: Event> T.consume(predicate: T.() -> Boolean, action: (T) -> Unit) {
  if (predicate(this)) {
    consume()
    action(this)
  }
}

object Events {
  fun eventTargetForChildren(node: Node, filter: (Node) -> Result): EventTarget {
    val dispatcher = dispatcherForChildren(node, filter)

//      println("dispatcher: $dispatcher")

    return EventTarget { tail ->
      val withAllChildren = dispatcher.fold(tail, { chain, dispatcher ->
        chain.append(dispatcher)
      })
      node.buildEventDispatchChain(withAllChildren)
    }
  }

  private fun dispatcherForChildren(node: Node, filter: (Node) -> Result): List<EventDispatcher> {
    return if (node is Parent) {
      return node.childrenUnmodifiable.flatMap { dispatcherFor(it, filter) }
    } else emptyList()
  }

  private fun dispatcherFor(node: Node, filter: (Node) -> Result): List<EventDispatcher> {
//      println("visit: $node")
    return when (filter(node)) {
      Result.Continue -> listOf(node.eventDispatcher) + dispatcherForChildren(node, filter)
      Result.OnlyChildren -> dispatcherForChildren(node, filter)
      Result.Stop -> listOf(node.eventDispatcher)
      Result.Skip -> emptyList()
    }
  }

  enum class Result {
    Continue,
    OnlyChildren,
    Skip,
    Stop
  }
}