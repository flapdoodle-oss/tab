package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Position
import de.flapdoodle.tab.model.Size

data class Resize(
    val id: Id<out Node>,
    val position: Position,
    val size: Size
) : SingleNodeModifier<Node>(
    id = id,
    change = { node -> node.resizeTo(position, size) }
)