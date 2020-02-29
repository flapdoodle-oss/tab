package de.flapdoodle.tab.bindings

import de.flapdoodle.tab.fx.SingleThreadMutex
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WeakChangeListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import org.fxmisc.easybind.EasyBind

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
}
