package de.flapdoodle.tab.bindings

import javafx.collections.ObservableList

class RegisteredObservableList<T : Any>(
    wrapped: ObservableList<T>,
    val registration: Registration
) : ObservableList<T> by wrapped