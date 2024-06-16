package de.flapdoodle.tab.model

import de.flapdoodle.kfx.collections.Diff
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.calculations.InputSlot
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.model.connections.Source
import de.flapdoodle.tab.model.modifier.ModifierFactory
import de.flapdoodle.tab.types.one
import java.nio.file.Path

data class Model(
    private val nodes: List<Node> = emptyList(),
    // dont persist
    val path: Path? = null,
) {
    private val nodesById = nodes.associateBy { it.id }

    fun apply(change: Change): Model {
        val modifier = ModifierFactory.changes(nodes, change)
        val result: List<Node> = modifier.fold(nodes) { list, m -> m.modify(list) }
        return copy(nodes = result)
    }

    fun nodes() = nodes

    fun node(id: Id<out Node>): Node {
        return requireNotNull(nodesById[id]) { "could not find node: $id" }
    }

    fun node(id: Id<out Node.Calculated<*>>): Node.Calculated<*> {
        return nodes.one { it.id == id } as Node.Calculated<*>
    }

    fun node(id: Id<out Node.Table<*>>): Node.Table<*> {
        return nodes.one { it.id == id } as Node.Table<*>
    }

    fun node(id: Id<out Node.Constants>): Node.Constants {
        return nodes.one { it.id == id } as Node.Constants
    }


    companion object {
        fun nodeChanges(old: Model, current: Model): de.flapdoodle.kfx.collections.Change<Node> {
            return Diff.between(old.nodes, current.nodes, Node::id)
        }

        fun connectionChanges(
            old: Model,
            current: Model
        ): de.flapdoodle.kfx.collections.Change<SourceAndDestination> {
            val oldInputs = nodeAndInputs(old.nodes)
            val currentInputs = nodeAndInputs(current.nodes)
            return Diff.between(oldInputs, currentInputs) { it.source to (it.destination to it.inputSlot.id) }
        }

        private fun nodeAndInputs(nodes: List<Node>): List<SourceAndDestination> {
            return nodes.filterIsInstance<Node.Calculated<out Comparable<*>>>().flatMap { node ->
                node.calculations.inputs().map { node to it }
            }.filter {
                it.second.source != null
            }.map {
                SourceAndDestination(it.second.source!! , it.first.id, it.second)
            }
        }

        data class SourceAndDestination(
            val source: Source,
            val destination: Id<Node.Calculated<*>>,
            val inputSlot: InputSlot<out Comparable<*>>
        )
    }

}