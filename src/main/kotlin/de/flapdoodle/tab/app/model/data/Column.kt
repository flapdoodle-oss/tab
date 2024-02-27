package de.flapdoodle.tab.app.model.data

import kotlin.reflect.KClass

data class Column<K : Comparable<K>, V : Any>(
    val name: String,
    val indexType: KClass<K>,
    val valueType: KClass<V>,
    val values: Map<K, V> = emptyMap(),
    val id: ColumnId<K> = ColumnId(indexType)
): Data() {
    fun add(index: K, value: V?): Column<K, V> {
        return copy(values = if (value != null) values + (index to value) else values - index)
    }

    operator fun get(index: K): V? {
        return values[index]
    }

    fun index() = values.keys
}