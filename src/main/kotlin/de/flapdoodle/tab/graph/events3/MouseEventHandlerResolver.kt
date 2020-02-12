package de.flapdoodle.tab.graph.events3

import de.flapdoodle.tab.annotations.KotlinCompilerFix_SAM_Helper

interface MouseEventHandlerResolver {
  fun onEnter(marker: Any): MouseEventHandler?

  companion object {
    @KotlinCompilerFix_SAM_Helper
    inline fun <reified T : IsMarker> forType(crossinline delegate: (T) -> MouseEventHandler): MouseEventHandlerResolver {
      return object : MouseEventHandlerResolver {
        override fun onEnter(marker: Any): MouseEventHandler? {
          return if (marker is T)
            delegate(marker)
          else
            null
        }
      }
    }
  }
}