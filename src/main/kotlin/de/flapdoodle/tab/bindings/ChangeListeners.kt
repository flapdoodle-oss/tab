package de.flapdoodle.tab.bindings

import de.flapdoodle.tab.fx.SingleThreadMutex
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener

fun <T> ChangeListener<T>.wrap(wrapper: (ChangeListener<T>) -> ChangeListener<T>): ChangeListener<T> {
  return wrapper(this)
}

fun <T> ChangeListener<T>.executeIn(mutex: SingleThreadMutex): ChangeListener<T> {
  return ChangeListeners.executeIn(mutex, this)
}

fun <T> ChangeListener<T>.tryExecuteIn(mutex: SingleThreadMutex): ChangeListener<T> {
  return ChangeListeners.tryExecuteIn(mutex, this)
}

object ChangeListeners {

  fun <T> executeIn(mutex: SingleThreadMutex, delegate: ChangeListener<T>): ChangeListener<T> {
    return MutexChangeListener(mutex, true, delegate)
  }

  fun <T> tryExecuteIn(mutex: SingleThreadMutex, delegate: ChangeListener<T>): ChangeListener<T> {
    return MutexChangeListener(mutex, false, delegate)
  }

  class MutexChangeListener<T>(
      private val mutex: SingleThreadMutex,
      private val forceExecute: Boolean,
      private val delegate: ChangeListener<T>
  ) : ChangeListener<T> {
    override fun changed(observable: ObservableValue<out T>, oldValue: T, newValue: T) {
      if (forceExecute) {
        mutex.execute {
          delegate.changed(observable, oldValue, newValue)
        }
      } else {
        mutex.tryExecute {
          delegate.changed(observable, oldValue, newValue)
        }
      }
    }
  }

  class Noop<T> : ChangeListener<T> {
    override fun changed(observable: ObservableValue<out T>?, oldValue: T, newValue: T) {

    }

  }
}