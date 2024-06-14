package de.flapdoodle.tab.model.modifier

import de.flapdoodle.tab.model.Node

data class AddNode(val node: Node) : Modifier() {
    override fun modify(nodes: List<Node>): List<Node> {
        val idMap = nodes.associateBy { it.id }
        require(idMap[node.id] == null) { "node ${node.id} already exist" }
        return nodes + node
    }
}