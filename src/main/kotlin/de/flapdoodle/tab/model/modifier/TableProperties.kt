package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node.Table
import de.flapdoodle.tab.model.Title

data class TableProperties(
    val id: Id<out Table<out Comparable<*>>>,
    val name: Title
) : TableModifier(
    id = id,
    change = { it.copy(name = name) }
)