package de.flapdoodle.tab.bindings

import de.flapdoodle.tab.fx.SingleThreadMutex
import javafx.collections.ListChangeListener
import javafx.collections.WeakListChangeListener

fun <T> ListChangeListener<T>.wrapByWeakChangeListener(): ListChangeListener<T> {
  return this.wrap(::WeakListChangeListener)
}

fun <T> ListChangeListener<T>.wrap(wrapper: (ListChangeListener<T>) -> ListChangeListener<T>): ListChangeListener<T> {
  return wrapper(this)
}

fun <T> ListChangeListener<T>.executeIn(mutex: SingleThreadMutex): ListChangeListener<T> {
  return ListChangeListeners.executeIn(mutex, this)
}

fun <T> ListChangeListener<T>.tryExecuteIn(mutex: SingleThreadMutex): ListChangeListener<T> {
  return ListChangeListeners.tryExecuteIn(mutex, this)
}

fun <T> ListChangeListener<T>.keepReference(to: Any): ListChangeListener<T> {
  return ListChangeListeners.KeepReferenceListChangeListener(
      keepReference = to,
      delegate = this
  )
}

object ListChangeListeners {

  fun <T> keepReferenceTo(reference: Any): ListChangeListener<T> {
    return KeepReferenceListChangeListener(reference)
  }

  fun <T> executeIn(mutex: SingleThreadMutex, delegate: ListChangeListener<T>): ListChangeListener<T> {
    return MutexListChangeListener(mutex, true, delegate)
  }

  fun <T> tryExecuteIn(mutex: SingleThreadMutex, delegate: ListChangeListener<T>): ListChangeListener<T> {
    return MutexListChangeListener(mutex, false, delegate)
  }

  fun <T> failOnModification(message: () -> String): ListChangeListener<T> {
    return ModificationNotAllowedListChangeListener(message)
  }

  class NoopListChangeListener<T> : ListChangeListener<T> {
    override fun onChanged(c: ListChangeListener.Change<out T>?) {

    }
  }

  class KeepReferenceListChangeListener<T>(
      private val keepReference: Any,
      private val delegate: ListChangeListener<T> = NoopListChangeListener()
  ) : ListChangeListener<T> {
    override fun onChanged(c: ListChangeListener.Change<out T>?) {
      delegate.onChanged(c)
    }
  }

  class MutexListChangeListener<T>(
      private val mutex: SingleThreadMutex,
      private val forceExecute: Boolean,
      private val delegate: ListChangeListener<T>
  ) : ListChangeListener<T> {
    override fun onChanged(c: ListChangeListener.Change<out T>?) {
      if (forceExecute) {
        mutex.execute {
          delegate.onChanged(c)
        }
      } else {
        mutex.tryExecute {
          delegate.onChanged(c)
        }
      }
    }
  }

  class ModificationNotAllowedListChangeListener<T>(
      private val message: () -> String
  ) : ListChangeListener<T> {
    override fun onChanged(c: ListChangeListener.Change<out T>?) {
      throw IllegalArgumentException(message())
    }
  }
}