package de.flapdoodle.tab.bindings

import javafx.beans.InvalidationListener
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
//    source.addListener(ChangeListener<Any> { _,_,newValue ->
//      println("source changed: $newValue")
//      invalidate()
//    }.wrap(::WeakChangeListener))
  }

  override fun computeValue(): ObservableList<T> {
    return FXCollections.observableArrayList(computed)
  }

  override fun getDependencies() = dependencies

  companion object {

    fun <S : Any, T : Any> newInstance(
        source: ObservableValue<S>,
        map: (S) -> List<T?>
    ): ObservableList<T> {
      val computed = FXCollections.observableArrayList<T>() as ObservableList<T>
      val srcChangeListener = ToListChangeListener(computed, map)

      source.addListener(srcChangeListener.wrap(::WeakChangeListener))
      computed.addAll(map(source.value))

      return object : ObservableList<T> by computed {}.apply {
        addListener(InvalidationListener {
          require(srcChangeListener != null) { "should never fail" }
        })
      }
    }
  }
}