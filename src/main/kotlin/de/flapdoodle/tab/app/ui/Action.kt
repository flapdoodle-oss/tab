package de.flapdoodle.tab.app.ui

import de.flapdoodle.tab.app.model.Tab2Model

sealed class Action {

    companion object {
        fun syncActions(old: Tab2Model, current: Tab2Model): List<Action> {
            val actions = emptyList<Action>()
            if (old != current) {
                val nodeChanges = Tab2Model.nodeChanges(old, current)


            }
            return actions
        }
    }
}