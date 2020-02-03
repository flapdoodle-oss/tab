package de.flapdoodle.tab.extensions

inline fun <reified T: R, R> R.change(action: (T) -> R): R {
  return if (this is T) {
    action(this)
  } else this
}

inline fun <reified T: R, R> R.matches(action: (T) -> Unit) {
  if (this is T) action(this)
}