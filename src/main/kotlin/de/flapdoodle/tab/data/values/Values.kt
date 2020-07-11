package de.flapdoodle.tab.data.values

interface Values<T : Any> {
  operator fun get(index: Int): T?
  fun size(): Int
  fun singleValue(): Boolean
}