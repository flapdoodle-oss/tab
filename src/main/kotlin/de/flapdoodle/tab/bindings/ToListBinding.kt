package de.flapdoodle.tab.bindings

import javafx.beans.binding.ListBinding
import javafx.beans.value.ObservableValue
import javafx.beans.value.WeakChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class ToListBinding<S : Any, T : Any>(
    source: ObservableValue<S>,
    map: (S) -> List<T?>
) : ListBinding<T>() {

  private var computed = FXCollections.observableArrayList<T>() as ObservableList<T>
  private val srcChangeListener = ToListChangeListener(computed, map)
  private val dependencies = FXCollections.singletonObservableList(source) as ObservableList<*>

  init {
    source.addListener(srcChangeListener.wrap(::WeakChangeListener))
    computed.addAll(map(source.value))
  }

  override fun computeValue() = computed
  override fun getDependencies() = dependencies
}