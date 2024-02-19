package de.flapdoodle.tab.app.model.data

import de.flapdoodle.kfx.types.Key
import kotlin.reflect.KClass

data class NamedType<T : Any>(
    val name: String,
    val type: KClass<T>,
    val id: Int = Key.nextId(type)
)