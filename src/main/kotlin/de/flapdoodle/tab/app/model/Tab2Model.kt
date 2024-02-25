package de.flapdoodle.tab.app.model

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.types.one

data class Tab2Model(
    val nodes: List<Node> = emptyList()
) {

    fun node(id: Id<out Node>): Node {
        return nodes.one { it.id == id }
    }
}