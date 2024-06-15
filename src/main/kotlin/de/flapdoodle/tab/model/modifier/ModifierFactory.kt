package de.flapdoodle.tab.model.modifier

import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.model.Node

object ModifierFactory {
    fun changes(nodes: List<Node>, change: Change): List<Modifier> {
        return when (change) {
            is Change.AddNode -> listOf(AddNode(change.node))
            is Change.RemoveNode -> Disconnect.removeSource(nodes, change.id) + RemoveNode(change.id)
            is Change.Move -> listOf(Move(change.id, change.position))
            is Change.Resize -> listOf(Resize(change.id, change.position, change.size))
            is Change.Connect -> listOf(Connect.map(nodes, change))
            is Change.Disconnect -> listOf(Disconnect.removeConnection(nodes, change.endId, change.input, change.source))
            is Change.Table -> tableChanges(nodes, change)
            else -> throw IllegalArgumentException("not implemented: $change")
        }
    }

    // VisibleForTesting
    internal fun tableChanges(nodes: List<Node>, change: Change.Table): List<Modifier> {
        return when (change) {
            is Change.Table.AddColumn<out Comparable<*>> -> listOf(AddColumn(change.id, change.column))
            is Change.Table.RemoveColumn -> Disconnect.removeSource(nodes, change.id, change.columnId)  + RemoveColumn(change.id, change.columnId)
            else -> throw IllegalArgumentException("not implemented: $change")
        }
    }
}