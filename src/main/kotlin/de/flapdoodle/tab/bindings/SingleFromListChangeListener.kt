package de.flapdoodle.tab.bindings

import javafx.beans.value.WritableObjectValue
import javafx.collections.ListChangeListener

class SingleFromListChangeListener<S, D>(
    private val dst: WritableObjectValue<D>,
    private val map: (List<S?>) -> D?
) : ListChangeListener<S> {

  override fun onChanged(change: ListChangeListener.Change<out S>) {
    println("XX SingleFromListChangeListener: changed with $map")
    val single =  map(change.list)
    dst.value = single
  }
}