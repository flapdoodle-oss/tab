package de.flapdoodle.tab.ui

import de.flapdoodle.kfx.collections.Change
import de.flapdoodle.kfx.collections.Diff
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.Data
import de.flapdoodle.tab.model.data.DataId
import de.flapdoodle.tab.model.data.SingleValue

sealed class Action {
    data class AddNode(val node: de.flapdoodle.tab.model.Node) : Action()
    data class AddInput(val id: Id<out de.flapdoodle.tab.model.Node>, val input: InputSlot<out Comparable<*>>) : Action()
    data class AddOutput(val id: Id<out de.flapdoodle.tab.model.Node>, val output: Data) : Action()
    data class AddConnection(val source: Source, val id: Id<out de.flapdoodle.tab.model.Node>, val input: Id<InputSlot<*>>) : Action()

    data class ChangeNode(val id: Id<out de.flapdoodle.tab.model.Node>, val node: de.flapdoodle.tab.model.Node) : Action()
    data class ChangeInput(
        val id: Id<out de.flapdoodle.tab.model.Node>,
        val input: Id<InputSlot<*>>,
        val change: InputSlot<out Comparable<*>>
    ) : Action()
    data class ChangeOutput(
        val id: Id<out de.flapdoodle.tab.model.Node>,
        val output: DataId,
        val change: Data
    ) : Action()

    data class RemoveConnection(val source: Source, val id: Id<out de.flapdoodle.tab.model.Node>, val input: Id<InputSlot<*>>) : Action()
    data class RemoveOutput(val id: Id<out de.flapdoodle.tab.model.Node>, val output: DataId) : Action()
    data class RemoveInput(val id: Id<out de.flapdoodle.tab.model.Node>, val input: Id<InputSlot<*>>) : Action()
    data class RemoveNode(val id: Id<out de.flapdoodle.tab.model.Node>) : Action()

    companion object {
        fun syncActions(old: Tab2Model, current: Tab2Model): List<Action> {
            var actions = emptyList<Action>()
            if (old != current) {
                val nodeChanges = Tab2Model.nodeChanges(old, current)
                val connectionChanges = Tab2Model.connectionChanges(old, current)

                actions = actions + connectionChanges.removed.map {
                    RemoveConnection(it.first, it.second.first, it.second.second.id)
                }

                actions = actions + nodeChanges.removed.flatMap { n ->
                    inputsOf(n).map { inputSlot ->
                        RemoveInput(n.id, inputSlot.id)
                    } + outputs(n).map { out ->
                        RemoveOutput(n.id, out.id)
                    } + listOf(RemoveNode(n.id))
                }

                actions = actions + nodeChanges.modified.flatMap { (old, current) ->
                    val inputChanges = inputChanges(old, current)
                    val outputChanges = outputChanges(old, current)

                    emptyList<Action>() +
                            inputChanges.removed.map { RemoveInput(old.id, it.id) } +
                            outputChanges.removed.map { RemoveOutput(old.id, it.id) } +
                            ChangeNode(current.id, current) +
                            inputChanges.modified.map { ChangeInput(current.id, it.second.id, it.second) } +
                            outputChanges.modified.map { ChangeOutput(current.id, it.second.id, it.second) } +
                            inputChanges.added.map { AddInput(current.id, it) } +
                            outputChanges.added.map { AddOutput(old.id, it) }
                }

                if (connectionChanges.modified.isNotEmpty()) {
                    throw IllegalArgumentException("unexpected change: ${connectionChanges.modified}")
                }

                actions = actions + nodeChanges.added.flatMap { n ->
                    listOf(AddNode(n)) + inputsOf(n).map { inputSlot ->
                        AddInput(n.id, inputSlot)
                    } + outputs(n).map { out ->
                        AddOutput(n.id, out)
                    }
                }

                actions = actions + connectionChanges.added.map {
                    AddConnection(it.first, it.second.first, it.second.second.id)
                }
            }
            return actions
        }

        private fun inputChanges(old: de.flapdoodle.tab.model.Node, current: de.flapdoodle.tab.model.Node): Change<InputSlot<*>> {
            return Diff.between(inputsOf(old), inputsOf(current), InputSlot<out Comparable<*>>::id)
        }

        private fun outputChanges(old: de.flapdoodle.tab.model.Node, current: de.flapdoodle.tab.model.Node): Change<Data> {
            return Diff.between(outputs(old), outputs(current), Data::id)
        }

        private fun inputsOf(node: de.flapdoodle.tab.model.Node): List<InputSlot<*>> {
            return when (node) {
                is de.flapdoodle.tab.model.Node.Calculated<out Comparable<*>> -> {
                    node.calculations.inputs()
                }

                else -> emptyList()
            }
        }

        private fun outputs(node: de.flapdoodle.tab.model.Node): List<Data> {
            return when (node) {
                is de.flapdoodle.tab.model.Node.Calculated<out Comparable<*>> -> {
                    outputsOfCalculatedNode(node)
                }

                is de.flapdoodle.tab.model.Node.Constants ->
                    node.values.values

                is de.flapdoodle.tab.model.Node.Table<out Comparable<*>> ->
                    node.columns.columns()
            }
        }

        private fun <K: Comparable<K>> outputsOfCalculatedNode(node: de.flapdoodle.tab.model.Node.Calculated<K>): List<Data> {
            val calculations = node.calculations

            val expectedColumns = calculations.tabular().map { it.destination() to it }
            val expectedValues = calculations.aggregations().map { it.destination() to it }
            val existingColumns = node.columns.columns().map { it.id }
            val existingValues = node.values.values.map { it.id }
            
            val missingColumns = expectedColumns.filter { !existingColumns.contains(it.first) }
            val missingValues = expectedValues.filter { !existingValues.contains(it.first) }

            return node.columns.columns() +
                    missingColumns.map { Column(it.second.name(), node.indexType, Unit::class, emptyMap(), it.first) } +
                    node.values.values +
                    missingValues.map { SingleValue(it.second.name(), Unit::class, null, it.first) }
        }
    }
}