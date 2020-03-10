package de.flapdoodle.tab.persist

interface ToPersistable<S: Any, T: Any> {
  fun toPersistable(source: S): T
}