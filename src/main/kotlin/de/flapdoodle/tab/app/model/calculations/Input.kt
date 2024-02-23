package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.kfx.types.Id

data class Input(
    val name: String,
    val id: Id<Input> = Id.Companion.nextId(Input::class)
)