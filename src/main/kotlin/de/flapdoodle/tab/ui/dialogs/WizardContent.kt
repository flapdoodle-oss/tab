package de.flapdoodle.tab.ui.dialogs

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.beans.value.ObservableValue

abstract class WizardContent<T: Any>: WeightGridPane() {
    abstract fun isValidProperty(): ObservableValue<Boolean>
    abstract fun result(): T?
    open fun abort(): T? = null
}