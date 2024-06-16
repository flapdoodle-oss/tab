package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Title

data class CalculationProperties(
    val id: Id<out Node.Calculated<out Comparable<*>>>,
    val name: Title
) : CalculationModifier(
    id = id,
    change = { it.copy(name = name) }
)