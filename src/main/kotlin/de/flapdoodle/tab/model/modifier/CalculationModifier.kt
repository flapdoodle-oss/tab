package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node

sealed class CalculationModifier(
    private val id: Id<out Node.Calculated<out Comparable<*>>>,
    private val change: (Node.Calculated<out Comparable<*>>) -> Node.Calculated<out Comparable<*>>
): SingleNodeModifier<Node.Calculated<out Comparable<*>>>(
    id = id,
    change = change
)