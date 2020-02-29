package de.flapdoodle.tab.bindings

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

class FlatmapListChangeListener<S, D>(
    private val dst: ObservableList<D>,
    private val map: (S?) -> List<D?>
) : ListChangeListener<S> {

  override fun onChanged(change: ListChangeListener.Change<out S>) {
    val list =  change.list.flatMap(map)
    if (list.size < dst.size) {
      dst.remove(list.size, dst.size)
    }
    (0 until dst.size).forEach {
      if (dst[it] != list[it]) {
        dst[it] = list[it]
      }
    }
    (dst.size until list.size).forEach {
      dst.add(list[it])
    }
  }
}