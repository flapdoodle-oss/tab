package de.flapdoodle.tab.bindings

import de.flapdoodle.tab.fx.SingleThreadMutex
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WeakChangeListener

fun <A : Any, B : Any, T : Any> ObservableValue<A>.mergeWith(other: ObservableValue<B>, map: (A, B) -> T): Pair<Registration, ObservableValue<T>> {
  return ObservableValues.merge(this, other) { a, b ->
    println("merge: $a and $b")
    if (a != null && b != null) map(a, b) else null
  }
}

fun <S : Any, T : Any> ObservableValue<S>.map(map: (S?) -> T?): Pair<Registration, ObservableValue<T>> {
  return ObservableValues.map(this, map)
}

object ObservableValues {

  fun <A : Any, B : Any, T : Any> merge(srcA: ObservableValue<A>, srcB: ObservableValue<B>, map: (A?, B?) -> T?): Pair<Registration, ObservableValue<T>> {
    val mutex = SingleThreadMutex()

    val dst = SimpleObjectProperty<T>()

    val wrappedListener = MergeListener(srcA, srcB) { a_val, b_val ->
      dst.value = map(a_val, b_val)
    }.executeIn(mutex)
    val srcAListener = wrappedListener.wrap(::WeakChangeListener)
    val srcBListener = wrappedListener.wrap(::WeakChangeListener)

    val dstChangeListener = ChangeListeners.failOnModification<T> { "$dst is synced" }
        .tryExecuteIn(mutex)
        .keepReference(wrappedListener)

    srcA.addListener(srcAListener)
    srcB.addListener(srcBListener)
    dst.addListener(dstChangeListener)

    mutex.execute {
      val a = srcA.value
      val b = srcB.value
      dst.set(map(a, b))
    }

    val registration = Registration {
      srcA.removeListener(srcAListener)
      srcB.removeListener(srcBListener)
      dst.removeListener(dstChangeListener)
    }

    return registration to dst
  }

  fun <S: Any, T: Any> map(src: ObservableValue<S>, map: (S?) -> T?): Pair<Registration, ObservableValue<T>> {
    val mutex = SingleThreadMutex()

    val dst = SimpleObjectProperty<T>()

    val wrappedListener = ChangeListener<S> { _, _, newValue ->
      dst.set(map(newValue))
    } .executeIn(mutex)

    val srcListener = wrappedListener.wrap(::WeakChangeListener)

    val dstChangeListener = ChangeListeners.failOnModification<T> { "$dst is synced" }
        .tryExecuteIn(mutex)
        .keepReference(wrappedListener)

    src.addListener(srcListener)
    dst.addListener(dstChangeListener)

    mutex.execute {
      val s = src.value
      dst.set(map(s))
    }

    val registration = Registration {
      src.removeListener(srcListener)
      dst.removeListener(dstChangeListener)
    }

    return registration to dst
  }

  private class MergeListener<A : Any, B : Any>(
      val a: ObservableValue<A>,
      val b: ObservableValue<B>,
      val onChange: (A?, B?) -> Unit
  ) : ChangeListener<Any> {

    override fun changed(observable: ObservableValue<out Any>, oldValue: Any?, newValue: Any?) {
      require(observable == a || observable == b) { "observable does not match: $observable != $a, $b" }
      onChange(a.value, b.value)
    }
  }
}