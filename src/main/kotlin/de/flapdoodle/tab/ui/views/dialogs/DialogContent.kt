package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.beans.value.ObservableValue

abstract class DialogContent<T: Any>: WeightGridPane() {
    abstract fun isValidProperty(): ObservableValue<Boolean>
    abstract fun result(): T?
    open fun abort(): T? = null
}