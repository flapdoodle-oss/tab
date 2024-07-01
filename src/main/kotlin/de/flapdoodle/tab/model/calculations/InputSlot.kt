package de.flapdoodle.tab.model.calculations

import de.flapdoodle.kfx.colors.HashedColors
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.connections.Source
import javafx.scene.paint.Color

data class InputSlot<K : Comparable<K>>(
    val name: String,
    val mapTo: Set<Variable> = emptySet(),
    val source: Source? = null,
    val id: Id<InputSlot<*>> = Id.nextId(InputSlot::class),
    val color: Color = HashedColors.hashedColor(name.hashCode() + id.hashCode())
) {
    val isColumnReference = name.startsWith('#')

    init {
        require(mapTo.all { it.isColumnReference == isColumnReference }) { "isColumnReference mismatch: $isColumnReference != $mapTo" }
    }
}