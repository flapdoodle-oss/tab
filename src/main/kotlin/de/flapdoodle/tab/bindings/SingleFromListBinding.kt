package de.flapdoodle.tab.bindings

import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class SingleFromListBinding<S : Any, T : Any>(
    private val source: ObservableList<S>,
    map: (List<S?>) -> T?
) : ObjectBinding<T>() {

  private var computed = SimpleObjectProperty<T>()
  private val srcChangeListener = SingleFromListChangeListener(computed, map)
//  private val dependencies = FXCollections.singletonObservableList(source) as ObservableList<*>

  init {
    source.addListener(srcChangeListener.wrapByWeakChangeListener())
    computed.set(map(source))
    computed.addListener { observable, oldValue, newValue ->
      this.invalidate()
    }
  }

  override fun computeValue(): T {
    return computed.get()
  }

//  override fun getDependencies() = dependencies
}