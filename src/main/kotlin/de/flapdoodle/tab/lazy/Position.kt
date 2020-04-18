package de.flapdoodle.tab.lazy

sealed class Position<T : Any> {
  class Before<T : Any>() : Position<T>()
  data class IndexedEntry<T : Any>(val index: Int, val value: T) : Position<T>()
  data class After <T: Any>(val index: Int) : Position<T>()
}