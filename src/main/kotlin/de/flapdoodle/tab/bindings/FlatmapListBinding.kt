package de.flapdoodle.tab.bindings

import javafx.collections.FXCollections
import javafx.collections.ObservableList

object FlatmapListBinding {
  @JvmStatic
  fun <S : Any, T : Any> newInstance(
      source: ObservableList<S>,
      map: (S?) -> List<T?>
  ): ObservableList<T> {
    val computed = FXCollections.observableArrayList<T>() as ObservableList<T>
    val srcChangeListener = FlatmapListChangeListener(computed, map)

    source.addListener(srcChangeListener.wrapByWeakChangeListener())
    computed.addAll(source.flatMap(map))

    return Wrapper(computed, srcChangeListener, source)
  }

  open class Wrapper<T>(
      delegate: ObservableList<T>,
      private val listener: FlatmapListChangeListener<out Any?, T>,
      private val source: ObservableList<out Any>
  ) : Bindings.ObservableListWrapper<T>(delegate)
}