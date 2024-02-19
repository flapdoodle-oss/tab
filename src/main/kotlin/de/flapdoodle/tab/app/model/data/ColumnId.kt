package de.flapdoodle.tab.app.model.data

import de.flapdoodle.kfx.types.Key
import kotlin.reflect.KClass

data class ColumnId<K: Any, V: Any>(
    val keyType: KClass<K>,
    val valueType: KClass<V>,
    val id: Int = Key.Companion.nextId(ColumnId::class)
)