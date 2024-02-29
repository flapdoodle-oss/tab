package de.flapdoodle.tab.app.ui.events

@FunctionalInterface
fun interface ModelEventListener {
  fun onEvent(event: ModelEvent): Boolean
}