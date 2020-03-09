package de.flapdoodle.tab.observable

interface Changeable<T: Any> {
  fun value(value: T)
}