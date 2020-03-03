package de.flapdoodle.tab.bindings

import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList

object ToListBinding {

  @JvmStatic
  fun <S : Any, T : Any> newInstance(
      source: ObservableValue<S>,
      map: (S) -> List<T?>
  ): ObservableList<T> {
    val computed = FXCollections.observableArrayList<T>() as ObservableList<T>
    val srcChangeListener = ToListChangeListener(computed, map)

    source.addListener(srcChangeListener.wrapByWeakChangeListener())
    computed.addAll(map(source.value))

    return Wrapper(computed, srcChangeListener, source)
  }

  open class Wrapper<T>(
      delegate: ObservableList<T>,
      private val listener: ToListChangeListener<out Any, T>,
      private val source: ObservableValue<out Any>
  ) : Bindings.ObservableListWrapper<T>(delegate)
}
