package de.flapdoodle.tab.graph.events

import de.flapdoodle.tab.annotations.KotlinCompilerFix_SAM_Helper
import javafx.event.EventTarget

interface MouseDragListener {
  fun drag(deltaX: Double, deltaY: Double, enter: EventTarget?)
  fun done()

  companion object {
    @KotlinCompilerFix_SAM_Helper
    inline operator fun invoke(crossinline done: () -> Unit, crossinline delegate: (Double, Double, EventTarget?) -> Unit): MouseDragListener {
      return object : MouseDragListener {
        override fun drag(deltaX: Double, deltaY: Double, enter: EventTarget?) {
          delegate(deltaX, deltaY, enter)
        }

        override fun done() {
          done()
        }
      }
    }
  }
}