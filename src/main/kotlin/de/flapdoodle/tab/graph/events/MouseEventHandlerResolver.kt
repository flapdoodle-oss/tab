package de.flapdoodle.tab.graph.events

fun interface MouseEventHandlerResolver {
  fun onEnter(marker: Any): MouseEventHandler?

  fun andThen(next: MouseEventHandlerResolver): MouseEventHandlerResolver {
    val that = this
    return object : MouseEventHandlerResolver {
      override fun onEnter(marker: Any) = that.onEnter(marker) ?: next.onEnter(marker)
    }
  }

  companion object {
    inline fun <reified T : IsMarker> forType(crossinline delegate: (T) -> MouseEventHandler): MouseEventHandlerResolver {
      return MouseEventHandlerResolver { marker ->
        if (marker is T)
          delegate(marker)
        else
          null
      }
    }
  }
}