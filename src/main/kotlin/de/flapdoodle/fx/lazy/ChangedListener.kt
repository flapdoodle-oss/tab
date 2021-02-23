package de.flapdoodle.fx.lazy

fun interface ChangedListener<T: Any> {
  fun hasChanged(value: LazyValue<T>)
}