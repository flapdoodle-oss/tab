package de.flapdoodle.tab.bindings

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableObjectValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import kotlin.math.sin

class SingleFromListChangeListener<S, D>(
    private val dst: WritableObjectValue<D>,
    private val map: (List<S?>) -> D?
) : ListChangeListener<S> {

  override fun onChanged(change: ListChangeListener.Change<out S>) {
    val single =  map(change.list)
    dst.value = single
  }
}