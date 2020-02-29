package de.flapdoodle.tab.bindings

import javafx.beans.binding.ListBinding
import javafx.beans.value.ObservableListValue
import javafx.beans.value.ObservableValue
import javafx.beans.value.WeakChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener

class FlatmapListBinding<S : Any, T : Any>(
    source: ObservableList<S>,
    map: (S?) -> List<T?>
) : ListBinding<T>() {

  private var computed = FXCollections.observableArrayList<T>() as ObservableList<T>
  private val srcChangeListener = FlatmapListChangeListener(computed, map)
  private val dependencies = FXCollections.singletonObservableList(source) as ObservableList<*>

  init {
    source.addListener(srcChangeListener.wrap(::WeakListChangeListener))
    computed.addAll(source.flatMap(map))
  }

  override fun computeValue() = computed
  override fun getDependencies() = dependencies
}