package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Node.Table
import de.flapdoodle.tab.model.Title

data class ConstantProperties(
    val id: Id<out Node.Constants>,
    val name: Title
) : ConstantModifier(
    id = id,
    change = { it.copy(name = name) }
)