package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.types.change

abstract class SingleNodeModifier<N: Node>(
    private val id: Id<out N>,
    private val change: (N) -> N
) : Modifier() {
    final override fun modify(nodes: List<Node>): List<Node> {
        var changeCount = 0
        val changed = nodes.change(Node::id, id) { node ->
            changeCount++
            change(node as N)
        }
        require(changeCount==1) { "more or less than one change: $changeCount for $id" }
        return changed
    }
}