package de.flapdoodle.tab.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableObjectValue

open class WritableObservableValue<T : Any, W>(
    private val wrapped: W
) : ObservableValue<T> by wrapped,
    WritableObjectValue<T> by wrapped
    where W : ObservableValue<T>,
          W : WritableObjectValue<T> {

  // compiler is wrong?
  override fun getValue(): T? {
    return wrapped.get()
  }
}