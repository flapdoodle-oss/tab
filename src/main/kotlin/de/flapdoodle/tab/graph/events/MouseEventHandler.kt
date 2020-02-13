package de.flapdoodle.tab.graph.events

import de.flapdoodle.tab.annotations.KotlinCompilerFix_SAM_Helper

interface MouseEventHandler {
  fun onEvent(mouseEvent: MappedMouseEvent, marker: IsMarker?): MouseEventHandler?

  companion object {
    @KotlinCompilerFix_SAM_Helper
    inline operator fun invoke(crossinline delegate: (MappedMouseEvent, IsMarker?) -> MouseEventHandler?): MouseEventHandler {
      return object : MouseEventHandler {
        override fun onEvent(mouseEvent: MappedMouseEvent, marker: IsMarker?): MouseEventHandler? {
          return delegate(mouseEvent,marker)
        }
      }
    }
  }
}