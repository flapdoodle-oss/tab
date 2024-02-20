package de.flapdoodle.tab.app.model.data

import de.flapdoodle.kfx.types.Key
import kotlin.reflect.KClass

data class SingleValueId<V : Any>(
    val valueType: KClass<V>,
    val id: Int = Key.Companion.nextId(SingleValueId::class)
)