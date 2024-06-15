package de.flapdoodle.tab.model.modifier

import de.flapdoodle.tab.model.Change
import de.flapdoodle.tab.model.Node

object ModifierFactory {
    fun changes(nodes: List<Node>, change: Change): List<Modifier> {
        return when (change) {
            is Change.AddNode -> listOf(AddNode(change.node))
            is Change.RemoveNode -> RemoveConnection.removeSource(nodes, change.id) + RemoveNode(change.id)
            is Change.Move -> listOf(Move(change.id, change.position))
            is Change.Resize -> listOf(Resize(change.id, change.position, change.size))
            else -> throw IllegalArgumentException("not implemented: $change")
        }
    }
}