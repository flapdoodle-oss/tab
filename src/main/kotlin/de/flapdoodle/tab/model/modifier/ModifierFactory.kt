package de.flapdoodle.tab.model.modifier

import de.flapdoodle.tab.model.Change
import de.flapdoodle.tab.model.Node

object ModifierFactory {
    fun changes(nodes: List<Node>, change: Change): List<Modifier> {
        return when (change) {
            is Change.AddNode -> listOf(AddNode(change.node))
            is Change.RemoveNode -> Disconnect.removeSource(nodes, change.id) + RemoveNode(change.id)
            is Change.Move -> listOf(Move(change.id, change.position))
            is Change.Resize -> listOf(Resize(change.id, change.position, change.size))
            is Change.Connect -> listOf(Connect.map(nodes, change))
            is Change.Disconnect -> listOf(Disconnect.remove(nodes, change.endId, change.input, change.source))
            else -> throw IllegalArgumentException("not implemented: $change")
        }
    }
}