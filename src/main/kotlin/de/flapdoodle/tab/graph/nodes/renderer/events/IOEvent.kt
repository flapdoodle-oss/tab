package de.flapdoodle.tab.graph.nodes.renderer.events

import tornadofx.*

data class IOEvent(
    val action: Action
) : FXEvent() {

  companion object {
    fun save(): IOEvent {
      return IOEvent(Action.Save)
    }

    fun load(): IOEvent {
      return IOEvent(Action.Load)
    }
  }

  sealed class Action {
    object Load : Action()
    object Save : Action()
  }
}