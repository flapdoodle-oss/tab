package de.flapdoodle.tab.ui

import de.flapdoodle.tab.model.changes.Change

fun interface ChangeListener {
    fun change(change: Change)
}