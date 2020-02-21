package de.flapdoodle.tab.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableObjectValue

class RegisteredWritableObservableValue<T : Any>(
    private val wrapped: SimpleObjectProperty<T>,
    val registration: Registration
) : ObservableValue<T> by wrapped, WritableObjectValue<T> by wrapped {
  // compiler is wrong?
  override fun getValue(): T {
    return wrapped.value
  }
}