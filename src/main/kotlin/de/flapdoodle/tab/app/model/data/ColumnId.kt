package de.flapdoodle.tab.app.model.data

import de.flapdoodle.kfx.types.Key
import kotlin.reflect.KClass

// remove valueType from Id, because calculation results may
// vary in type
data class ColumnId<K: Comparable<K>>(
    val indexType: KClass<in K>,
//    val valueType: KClass<V>,
    val id: Int = Key.Companion.nextId(ColumnId::class)
): DataId()