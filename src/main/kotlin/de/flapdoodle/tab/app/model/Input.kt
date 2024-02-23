package de.flapdoodle.tab.app.model

import de.flapdoodle.kfx.types.Id
import kotlin.reflect.KClass

data class Input(
    val name: String,
    val id: Id<Input> = Id.Companion.nextId(Input::class)
)