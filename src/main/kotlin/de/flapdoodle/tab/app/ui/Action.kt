package de.flapdoodle.tab.app.ui

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.model.calculations.InputSlot
import de.flapdoodle.tab.app.model.connections.Source

sealed class Action {
    data class AddNode(val node: Node): Action()
    data class AddInput(val id: Id<out Node>, val input: InputSlot<out Comparable<*>>): Action()
    data class AddConnection(val source: Source, val id: Id<out Node>, val input: Id<InputSlot<*>>): Action()

    data class ChangeNode(val id: Id<out Node>, val node: Node): Action()
    data class ChangeInput(val id: Id<out Node>, val input: Id<InputSlot<*>>, val change: InputSlot<out Comparable<*>>): Action()

    data class RemoveConnection(val source: Source, val id: Id<out Node>, val input: Id<InputSlot<*>>): Action()
    data class RemoveInput(val id: Id<out Node>, val input: Id<InputSlot<*>>): Action()
    data class RemoveNode(val id: Id<out Node>): Action()

    companion object {
        fun syncActions(old: Tab2Model, current: Tab2Model): List<Action> {
            var actions = emptyList<Action>()
            if (old != current) {
                val nodeChanges = Tab2Model.nodeChanges(old, current)
                val connectionChanges = Tab2Model.connectionChanges(old, current)

                actions = actions + nodeChanges.removed.flatMap { n ->
                    when (n) {
                        is Node.Calculated<out Comparable<*>> -> {
                            emptyList<Action>()
                        }
                        else -> {
                            emptyList<Action>()
                        }
                    } + listOf(RemoveNode(n.id))
                }

                actions = actions + nodeChanges.added.flatMap { n ->
                    listOf(AddNode(n)) + when (n) {
                        is Node.Calculated<out Comparable<*>> -> {
                            emptyList<Action>()
                        }
                        else -> {
                            emptyList<Action>()
                        }
                    }
                }


            }
            return actions
        }
    }
}