package de.flapdoodle.tab.app.ui

import de.flapdoodle.tab.app.model.change.ModelChange

fun interface ModelChangeListener {
    fun change(modelChange: ModelChange)
}