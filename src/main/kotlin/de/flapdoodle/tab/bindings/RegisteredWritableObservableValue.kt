package de.flapdoodle.tab.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableObjectValue

@Deprecated("remove")
class RegisteredWritableObservableValue<T : Any>(
    private val wrapped: SimpleObjectProperty<T>,
    val registration: Registration
) : WritableObservableValue<T, SimpleObjectProperty<T>>(wrapped)