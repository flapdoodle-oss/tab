package de.flapdoodle.tab.graph.events3

import de.flapdoodle.tab.annotations.KotlinCompilerFix_SAM_Helper
import javafx.scene.input.MouseEvent

interface MouseEventHandler {
  fun onEvent(mouseEvent: MouseEvent, marker: IsMarker?): MouseEventHandler?

  companion object {
    @KotlinCompilerFix_SAM_Helper
    inline operator fun invoke(crossinline delegate: (MouseEvent, IsMarker?) -> MouseEventHandler?): MouseEventHandler {
      return object : MouseEventHandler {
        override fun onEvent(mouseEvent: MouseEvent, marker: IsMarker?): MouseEventHandler? {
          return delegate(mouseEvent,marker)
        }
      }
    }
  }
}