package de.flapdoodle.tab.app.model

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.types.Change
import de.flapdoodle.tab.types.one

data class Tab2Model(
    val nodes: List<Node> = emptyList()
) {
    private val nodesById = nodes.associateBy { it.id }
    init {
        val collisions = nodes.groupBy { it.id }.filter { it.value.size>1 }
        require(collisions.isEmpty()) { "node id collisions: ${collisions.keys}"}
    }

    fun addNode(node: Node): Tab2Model {
        return copy(nodes = nodes + node)
    }



    fun node(id: Id<out Node>): Node {
        return nodes.one { it.id == id }
    }

    fun node(id: Id<out Node.Calculated<*>>): Node.Calculated<*> {
        return nodes.one { it.id == id } as Node.Calculated<*>
    }

    companion object {
        fun nodeChanges(old: Tab2Model, current: Tab2Model): Change<Node> {
            return Change.change(old.nodes, current.nodes, Node::id)
        }
    }
}