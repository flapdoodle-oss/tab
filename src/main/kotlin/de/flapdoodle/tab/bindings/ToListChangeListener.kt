package de.flapdoodle.tab.bindings

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList

class ToListChangeListener<S, D>(
    private val dst: ObservableList<D>,
    private val map: (S) -> List<D?>
) : ChangeListener<S> {
  override fun changed(observable: ObservableValue<out S>, oldValue: S?, newValue: S?) {
    val list = if (newValue != null) map(newValue) else emptyList()
    if (list.size < dst.size) {
      dst.remove(list.size, dst.size)
    }
    (dst.size until list.size).forEach {
      dst.add(list[it])
    }
    (0 until dst.size).forEach {
      if (dst[it] != list[it]) {
        dst[it] = list[it]
      }
    }
  }
}