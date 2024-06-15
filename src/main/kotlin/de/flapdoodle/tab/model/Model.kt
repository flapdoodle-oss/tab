package de.flapdoodle.tab.model

import de.flapdoodle.tab.model.change.ModelChange
import de.flapdoodle.tab.model.modifier.ModifierFactory
import java.nio.file.Path

data class Model(
    // dont persist
    val path: Path? = null,
    private val nodes: List<Node> = emptyList(),
) {

    fun apply(change: Change): Model {
        val modifier = ModifierFactory.changes(nodes, change)
        val result: List<Node> = modifier.fold(nodes) { list, m -> m.modify(list) }
        return copy(nodes = result)
    }

    @Deprecated("use Change")
    fun apply(change: ModelChange): Model {
        return copy(nodes = nodes.map { it.apply(change) })
    }

    fun nodes() = nodes
}