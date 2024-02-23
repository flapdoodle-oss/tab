package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.connections.Source

data class InputSlot(
    val name: String,
    val mapTo: Set<Variable> = emptySet(),
    val source: Source? = null,
    val id: Id<InputSlot> = Id.nextId(InputSlot::class)
) {
}