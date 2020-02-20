package de.flapdoodle.tab.bindings

import javafx.beans.InvalidationListener
import javafx.beans.binding.ListBinding
import javafx.beans.value.ObservableObjectValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

fun <S: Any, T: Any> ObservableObjectValue<S>.listBinding(transformation: (S) -> List<T?>): ObjectToListBinding<T, S> {
  return ObjectToListBinding(this, transformation)
}

class ObjectToListBinding<T: Any, S: Any>(
    private val source: ObservableObjectValue<S>,
    private val transformation: (S) -> List<T?>
) : ListBinding<T>() {
  override fun computeValue(): ObservableList<T> {
    val ret = FXCollections.observableArrayList<T>()
    source.addListener { observable, oldValue, newValue ->
      ret.setAll(transformation(newValue))
    }
    source.addListener(InvalidationListener {
      ret.invalidate()
    })
    return ret
  }
}