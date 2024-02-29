package de.flapdoodle.tab.app.model.data

import kotlin.reflect.KClass

data class Column<K : Comparable<K>, V : Any>(
    val name: String,
    val indexType: KClass<K>,
    val valueType: KClass<V>,
    val values: Map<K, V> = emptyMap(),
    override val id: ColumnId<K> = ColumnId(indexType)
): Data() {
    fun add(index: K, value: V?): Column<K, V> {
        return copy(values = if (value != null) values + (index to value) else values - index)
    }

    operator fun get(index: K): V? {
        return values[index]
    }

    fun index() = values.keys

    fun set(newValues: Map<K, out Any>): Column<K, V> {
        var map = values
        newValues.forEach { k, v ->
            if (valueType.isInstance(v)) {
                map = (map - k) + (k to (v as V))
            } else {
                throw IllegalArgumentException("can not set $k:$v to $this")
            }
        }
        return copy(values = map)
    }
}