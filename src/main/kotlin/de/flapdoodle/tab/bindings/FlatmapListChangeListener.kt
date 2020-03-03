package de.flapdoodle.tab.bindings

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

class FlatmapListChangeListener<S, D>(
    private val dst: ObservableList<D>,
    private val map: (S?) -> List<D?>
) : ListChangeListener<S> {

  override fun onChanged(change: ListChangeListener.Change<out S>) {
    ListChanges.applyChanges(dst, change.list.flatMap(map))
  }
}