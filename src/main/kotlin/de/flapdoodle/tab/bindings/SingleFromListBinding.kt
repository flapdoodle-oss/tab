package de.flapdoodle.tab.bindings

import javafx.beans.binding.ListBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableListValue
import javafx.beans.value.ObservableValue
import javafx.beans.value.WeakChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener

class SingleFromListBinding<S : Any, T : Any>(
    source: ObservableList<S>,
    map: (List<S?>) -> T?
) : ObjectBinding<T>() {

  private var computed = SimpleObjectProperty<T>()
  private val srcChangeListener = SingleFromListChangeListener(computed, map)
  private val dependencies = FXCollections.singletonObservableList(source) as ObservableList<*>

  init {
    source.addListener(srcChangeListener.wrap(::WeakListChangeListener))
    computed.set(map(source))
    computed.addListener { observable, oldValue, newValue ->
      this.invalidate()
    }
  }

  override fun computeValue(): T {
    return computed.get()
  }

  override fun getDependencies() = dependencies
}