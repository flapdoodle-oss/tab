package de.flapdoodle.tab.bindings

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList

class ToListChangeListener<S, D>(
    private val dst: ObservableList<D>,
    private val map: (S) -> List<D?>
) : ChangeListener<S> {
  override fun changed(observable: ObservableValue<out S>, oldValue: S?, newValue: S?) {
    ListChanges.applyChanges(dst, if (newValue != null) map(newValue) else emptyList())
  }
}