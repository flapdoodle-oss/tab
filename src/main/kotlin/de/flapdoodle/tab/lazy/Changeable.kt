package de.flapdoodle.tab.lazy

interface Changeable<T: Any> {
  fun value(value: T)
}