package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Position

data class Move(
    val id: Id<out Node>,
    val position: Position,
) : SingleNodeModifier<Node>(
    id = id,
    change = { node -> node.moveTo(position) }
)