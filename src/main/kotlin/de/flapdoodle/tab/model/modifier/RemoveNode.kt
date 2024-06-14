package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node

data class RemoveNode(val id: Id<out Node>) : Modifier() {
    override fun modify(nodes: List<Node>): List<Node> {
        val without = nodes.filter { it.id != id }
        require(without.size < nodes.size) { "node not found: $id" }
        return without
    }
}