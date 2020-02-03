package de.flapdoodle.tab.graph.events

import de.flapdoodle.tab.annotations.KotlinCompilerFix_SAM_Helper
import javafx.event.EventTarget

interface MouseDragListenerLookup {
  fun listenerFor(target: EventTarget): MouseDragListener?

  fun andThen(other: MouseDragListenerLookup): MouseDragListenerLookup {
    val that = this
    return MouseDragListenerLookup { event ->
      that.listenerFor(event) ?: other.listenerFor(event)
    }
  }

  companion object {
    @KotlinCompilerFix_SAM_Helper
    inline operator fun invoke(crossinline delegate: (EventTarget) -> MouseDragListener?): MouseDragListenerLookup {
      return object : MouseDragListenerLookup {
        override fun listenerFor(target: EventTarget): MouseDragListener? {
          return delegate(target)
        }
      }
    }

    inline fun <reified T : EventTarget> forType(crossinline  listenerFactory: (T) -> MouseDragListener): MouseDragListenerLookup {
      return MouseDragListenerLookup { eventTarget ->
        if (eventTarget is T) {
          listenerFactory(eventTarget)
        } else null
      }
    }
  }

}