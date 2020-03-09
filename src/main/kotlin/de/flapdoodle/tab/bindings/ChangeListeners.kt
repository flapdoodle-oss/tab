package de.flapdoodle.tab.bindings

import de.flapdoodle.tab.fx.SingleThreadMutex
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WeakChangeListener

fun <T> ChangeListener<T>.wrapByWeakChangeListener(): ChangeListener<T> {
  return this.wrap(::WeakChangeListener)
}

fun <T> ChangeListener<T>.wrap(wrapper: (ChangeListener<T>) -> ChangeListener<T>): ChangeListener<T> {
  return wrapper(this)
}

