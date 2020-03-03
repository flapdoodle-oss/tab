package de.flapdoodle.tab.bindings

import de.flapdoodle.tab.fx.SingleThreadMutex
import javafx.collections.ObservableList

fun <S : Any, D : Any> ObservableList<D>.syncFrom(src: ObservableList<S>, map: (S?) -> D?): Registration {
  return ObservableLists.addMappedSync(src, this, map)
}

object ObservableLists {

  fun <S : Any, D : Any> addMappedSync(src: ObservableList<S>, dst: ObservableList<D>, map: (S?) -> D?): Registration {
    val mutex = SingleThreadMutex()

    val wrappedSrcChangeListener = ListChangeListeners.executeIn(mutex, MappingListChangeListener(dst, map))
    val srcChangeListener = wrappedSrcChangeListener.wrapByWeakChangeListener()

    val dstChangeListener = ListChangeListeners.failOnModification<D> { "$dst is synced" }
        .tryExecuteIn(mutex)
        .keepReference(wrappedSrcChangeListener)
        .keepReference(src)

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
}
