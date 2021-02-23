package de.flapdoodle.tab.observable

fun interface ChangeListener<T : Any> {
  fun changed(src: AObservable<T>, old: T, new: T)
}