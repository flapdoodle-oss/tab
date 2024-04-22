package de.flapdoodle.tab.ui

import de.flapdoodle.tab.model.change.ModelChange

fun interface ModelChangeListener {
    fun change(modelChange: ModelChange)
}