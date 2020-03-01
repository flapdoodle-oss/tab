package de.flapdoodle.tab.bindings

import javafx.beans.binding.ListBinding
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
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
    source.addListener(ListChangeListener<Any> {
      invalidate()
    }.wrap(::WeakListChangeListener))
  }

  override fun computeValue(): ObservableList<T> {
    return FXCollections.observableArrayList(computed)
  }
  override fun getDependencies() = dependencies

  companion object {
    fun <S : Any, T : Any> newInstance(
        source: ObservableList<S>,
        map: (S?) -> T?
    ): ObservableList<T> {

      val computed = FXCollections.observableArrayList<T>() as ObservableList<T>
      val srcChangeListener = MappingListChangeListener(computed, map)
      source.addListener(srcChangeListener.wrap(::WeakListChangeListener))
      computed.addAll(source.map(map))

      return object : ObservableList<T> by computed {
        private val listender = srcChangeListener
      }
    }
  }
}