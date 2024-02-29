package de.flapdoodle.tab.app.ui

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.model.calculations.InputSlot
import de.flapdoodle.tab.app.model.connections.Source
import de.flapdoodle.tab.app.model.data.Data
import de.flapdoodle.tab.app.model.data.DataId

sealed class Action {
    data class AddNode(val node: Node) : Action()
    data class AddInput(val id: Id<out Node>, val input: InputSlot<out Comparable<*>>) : Action()
    data class AddOutput(val id: Id<out Node>, val output: Data) : Action()
    data class AddConnection(val source: Source, val id: Id<out Node>, val input: Id<InputSlot<*>>) : Action()

    data class ChangeNode(val id: Id<out Node>, val node: Node) : Action()
    data class ChangeInput(
        val id: Id<out Node>,
        val input: Id<InputSlot<*>>,
        val change: InputSlot<out Comparable<*>>
    ) : Action()

    data class RemoveConnection(val source: Source, val id: Id<out Node>, val input: Id<InputSlot<*>>) : Action()
    data class RemoveOutput(val id: Id<out Node>, val output: DataId) : Action()
    data class RemoveInput(val id: Id<out Node>, val input: Id<InputSlot<*>>) : Action()
    data class RemoveNode(val id: Id<out Node>) : Action()

    companion object {
        fun syncActions(old: Tab2Model, current: Tab2Model): List<Action> {
            var actions = emptyList<Action>()
            if (old != current) {
                val nodeChanges = Tab2Model.nodeChanges(old, current)
                val connectionChanges = Tab2Model.connectionChanges(old, current)

                actions = actions + nodeChanges.removed.flatMap { n ->
                    inputsOf(n).map { inputSlot ->
                        RemoveInput(n.id, inputSlot.id)
                    } + outputs(n).map { out ->
                        RemoveOutput(n.id, out.id)
                    } + listOf(RemoveNode(n.id))
                }

                actions = actions + nodeChanges.added.flatMap { n ->
                    listOf(AddNode(n)) + inputsOf(n).map { inputSlot ->
                        AddInput(n.id, inputSlot)
                    } + outputs(n).map { out ->
                        AddOutput(n.id, out)
                    }
                }
            }
            return actions
        }

        private fun inputsOf(node: Node): List<InputSlot<*>> {
            return when (node) {
                is Node.Calculated<out Comparable<*>> -> {
                    node.calculations.inputs
                }

                else -> emptyList()
            }
        }

        private fun outputs(node: Node): List<Data> {
            return when (node) {
                is Node.Calculated<out Comparable<*>> ->
                    node.columns.columns + node.values.values

                is Node.Constants ->
                    node.values.values

                is Node.Table<out Comparable<*>> ->
                    node.columns.columns
            }
        }

//        fun inputChanges(old: Node, current: Node): Change<Pair<Id<Node.Calculated<*>>, InputSlot<out Comparable<*>>>> {
//            require(old.id == current.id) { "id mismatch: ${old.id} != ${current.id}"}
//            if (old is Node.Calculated<out Comparable<*>> && current is Node.Calculated<out Comparable<*>>) {
//                return inputChanges(old, current)
//            }
//            return Change.empty()
//        }
//
//        private fun inputChanges(
//            old: Node.Calculated<out Comparable<*>>,
//            current: Node.Calculated<out Comparable<*>>
//        ): Change<Pair<Id<Node.Calculated<*>>, InputSlot<out Comparable<*>>>> {
//            val change = Change.diff(old.calculations.inputs, current.calculations.inputs, InputSlot<out Comparable<*>>::id)
//            return Change(
//                removed = change.removed.map { old.id to it }.toSet(),
//                notChanged = change.notChanged.map { current.id to it }.toSet(),
//                modified = change.modified.map { (o,c) ->
//                    (old.id to o) to (current.id to c)
//                }.toSet(),
//                added = change.added.map { current.id to it }.toSet()
//            )
//        }
    }
}