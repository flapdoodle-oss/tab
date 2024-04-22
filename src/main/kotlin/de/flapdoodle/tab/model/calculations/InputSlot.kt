package de.flapdoodle.tab.model.calculations

import de.flapdoodle.kfx.controls.colors.HashedColors
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.connections.Source
import javafx.scene.paint.Color

// kommt hier der Interpolator rein?
data class InputSlot<K: Comparable<K>>(
    val name: String,
    val mapTo: Set<Variable> = emptySet(),
    val source: Source? = null,
    val id: Id<InputSlot<*>> = Id.nextId(InputSlot::class),
    val color: Color = HashedColors.hashedColor(name.hashCode() + id.hashCode())
) {
}