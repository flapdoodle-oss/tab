package de.flapdoodle.tab.app.model.data

data class Column<K : Any, V : Any>(
    val name: String,
    val id: ColumnId<K, V>,
    val values: Map<K, V> = emptyMap()
) {
    fun add(index: K, value: V?): Column<K, V> {
        return copy(values = if (value != null) values + (index to value) else values - index)
    }

    operator fun get(index: K): V? {
        return values[index]
    }
}