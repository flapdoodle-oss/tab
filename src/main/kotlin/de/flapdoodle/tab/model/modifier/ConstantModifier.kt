package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node

sealed class ConstantModifier(
    private val id: Id<out Node.Constants>,
    private val change: (Node.Constants) -> Node.Constants
): SingleNodeModifier<Node.Constants>(
    id = id,
    change = change
)