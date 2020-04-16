package de.flapdoodle.tab.lazy

sealed class PositionedEntry<T : Any> {
  class Start<T : Any>() : PositionedEntry<T>()
  data class WithIndex<T : Any>(val index: Int, val value: T) : PositionedEntry<T>()
  data class End <T: Any>(val index: Int) : PositionedEntry<T>()
}