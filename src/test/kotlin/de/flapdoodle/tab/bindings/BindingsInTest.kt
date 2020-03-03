package de.flapdoodle.tab.bindings

import javafx.beans.InvalidationListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList

fun <S : Any> ObservableValue<S>.gcAbleCopy(): ObservableValue<S> {
  return Bindings.map(this) { it }
}

fun <S : Any> ObservableList<S>.gcAbleCopy(): ObservableList<S> {
  val ret = FXCollections.observableArrayList<S>(this)
  val listener = ListChangeListener<S> {
    ret.setAll(this)
  }
  this.addListener(listener)
  ret.addListener(InvalidationListener {
    require(listener.hashCode() != 0) { "should never fail, hack to keep reference" }
  })
  return ret
}
