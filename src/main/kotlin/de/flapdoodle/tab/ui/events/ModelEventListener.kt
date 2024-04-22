package de.flapdoodle.tab.ui.events

@FunctionalInterface
fun interface ModelEventListener {
  fun onEvent(event: ModelEvent): Boolean
}