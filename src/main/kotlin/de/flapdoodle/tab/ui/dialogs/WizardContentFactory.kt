package de.flapdoodle.tab.ui.dialogs

fun interface WizardContentFactory<T: Any> {
    fun content(value: T): WizardContent<T>
}