package de.flapdoodle.tab.bindings

import javafx.beans.binding.ListBinding
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener

class MappingListBinding<S : Any, T : Any>(
    source: ObservableList<S>,
    map: (S?) -> T?
) : ListBinding<T>() {

  private var computed = FXCollections.observableArrayList<T>() as ObservableList<T>
  private val srcChangeListener = MappingListChangeListener(computed, map)
  private val dependencies = FXCollections.singletonObservableList(source) as ObservableList<*>

  init {
    source.addListener(srcChangeListener.wrap(::WeakListChangeListener))
    computed.addAll(source.map(map))
  }

  override fun computeValue() = computed
  override fun getDependencies() = dependencies
}