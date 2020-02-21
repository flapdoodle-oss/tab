package de.flapdoodle.tab.bindings

import de.flapdoodle.tab.fx.SingleThreadMutex
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WeakChangeListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener

fun <S : Any, D : Any> ObservableValue<S>.mapToList(map: (S) -> List<D?>): RegisteredObservableList<D> {
  return ObservableLists.mapToList(this, map)
}

fun <S : Any, D : Any> ObservableList<D>.syncFrom(src: ObservableList<S>, map: (S?) -> D?): Registration {
  return ObservableLists.addMappedSync(src, this, map)
}

object ObservableLists {

  fun <S : Any, D : Any> addMappedSync(src: ObservableList<S>, dst: ObservableList<D>, map: (S?) -> D?): Registration {
    val mutex = SingleThreadMutex()

    val wrappedSrcChangeListener = ListChangeListeners.executeIn(mutex, MappingListChangeListener(dst, map))
    val srcChangeListener = wrappedSrcChangeListener.wrap(::WeakListChangeListener)

    val dstChangeListener = ListChangeListeners.failOnModification<D> { "$dst is synced" }
        .tryExecuteIn(mutex)
        .keepReference(wrappedSrcChangeListener)

    src.addListener(srcChangeListener)
    dst.addListener(dstChangeListener)

    mutex.execute {
      dst.clear()
      src.forEach { dst.add(map(it)) }
    }

    return Registration {
      src.removeListener(srcChangeListener)
      dst.removeListener(dstChangeListener)
    }
  }

  fun <S : Any, D : Any> map(src: ObservableList<S>, map: (S?) -> D?): RegisteredObservableList<D> {
    val ret = FXCollections.observableArrayList<D>()
    val registration = addMappedSync(src, ret, map)
    return RegisteredObservableList(ret, registration)
  }

  fun <S : Any, D : Any> mapToList(src: ObservableValue<S>, map: (S) -> List<D?>): RegisteredObservableList<D> {
    val mutex = SingleThreadMutex()

    val dst = FXCollections.observableArrayList<D>()

    val wrappedChangeListener = ToListChangeListener(dst, map).executeIn(mutex)
    val srcChangeListener = wrappedChangeListener.wrap(::WeakChangeListener)

    val dstChangeListener = ListChangeListeners.failOnModification<D> { "$dst is synced" }
        .tryExecuteIn(mutex)
        .keepReference(wrappedChangeListener)

    src.addListener(srcChangeListener)
    dst.addListener(dstChangeListener)

    mutex.execute {
      val s = src.value
      if (s!=null) {
        dst.addAll(map(s))
      }
    }

    val registration = Registration {
      src.removeListener(wrappedChangeListener)
      dst.removeListener(dstChangeListener)
    }
    return RegisteredObservableList(dst, registration)
  }

  private fun <D : Any, S : Any> changeProtectionListener(keepReference: ChangeListener<S>, mutex: SingleThreadMutex, dst: ObservableList<D>?, src: ObservableValue<S>): ListChangeListener<D> {
    return object : ListChangeListener<D> {
      private val keepReference = keepReference
      override fun onChanged(it: ListChangeListener.Change<out D>) {
        mutex.tryExecute {
          throw IllegalArgumentException("$dst is synced with $src")
        }
      }
    }
  }
}
