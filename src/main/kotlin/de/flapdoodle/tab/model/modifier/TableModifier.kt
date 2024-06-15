package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node

sealed class TableModifier(
    private val id: Id<out Node.Table<out Comparable<*>>>,
    private val change: (Node.Table<out Comparable<*>>) -> Node.Table<out Comparable<*>>
): SingleNodeModifier<Node.Table<out Comparable<*>>>(
    id = id,
    change = change
)