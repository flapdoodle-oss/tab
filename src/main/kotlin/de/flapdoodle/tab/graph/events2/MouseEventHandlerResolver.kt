package de.flapdoodle.tab.graph.events2

import de.flapdoodle.tab.annotations.KotlinCompilerFix_SAM_Helper
import javafx.event.EventTarget

interface MouseEventHandlerResolver {
  fun onEnter(eventTarget: EventTarget): MouseEventHandler?

  companion object {
    @KotlinCompilerFix_SAM_Helper
    inline fun <reified T : EventTarget> forType(crossinline delegate: (T) -> MouseEventHandler): MouseEventHandlerResolver {
      return object : MouseEventHandlerResolver {
        override fun onEnter(eventTarget: EventTarget): MouseEventHandler? {
          return if (eventTarget is T) {
            return delegate(eventTarget)
          } else null
        }
      }
    }
  }
}