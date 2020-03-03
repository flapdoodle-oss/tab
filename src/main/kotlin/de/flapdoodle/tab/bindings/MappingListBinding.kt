package de.flapdoodle.tab.bindings

import javafx.collections.FXCollections
import javafx.collections.ObservableList

object MappingListBinding {

  @JvmStatic
  fun <S : Any, T : Any> newInstance(
      source: ObservableList<S>,
      map: (S?) -> T?
  ): ObservableList<T> {

    val computed = FXCollections.observableArrayList<T>() as ObservableList<T>
    val srcChangeListener = MappingListChangeListener(computed, map)
    source.addListener(srcChangeListener.wrapByWeakChangeListener())
    computed.addAll(source.map(map))

    return Wrapper(computed, srcChangeListener, source)
  }

  class Wrapper<T>(
      delegate: ObservableList<T>,
      private val listener: MappingListChangeListener<out Any?, T>,
      private val source: ObservableList<out Any>
  ) : Bindings.ObservableListWrapper<T>(delegate)
}