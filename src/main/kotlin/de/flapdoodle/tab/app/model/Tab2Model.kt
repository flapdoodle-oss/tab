package de.flapdoodle.tab.app.model

import de.flapdoodle.kfx.types.Id

data class Tab2Model(
    val nodes: List<Node> = emptyList()
) {

    fun node(id: Id<out Node>): Node {
        val matching = nodes.filter { it.id == id }
        require(matching.size == 1) {"more or less then one node with $id found: $matching"}
        return  matching[0]
    }
}