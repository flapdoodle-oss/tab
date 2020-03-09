package de.flapdoodle.tab.observable

interface AObservable<T: Any> {
  fun value(): T

  fun addListener(listener: ChangeListener<T>)
  fun removeListener(listener: ChangeListener<T>)

  class Wrapper<T: Any>(
      delegate: AObservable<T>,
      private val keepReference: Any
  ) : AObservable<T> by delegate
}