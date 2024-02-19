package de.flapdoodle.tab.app.model.data

import java.util.SortedSet

data class Columns<K: Comparable<K>>(
    val index: Set<K> = emptySet(),
    val columns: List<Column<K, *>> = emptyList()
) {
    init {
        val collisions = columns.groupBy { it.id }
            .filter { it.value.size > 1 }
            .keys
        require(collisions.isEmpty()) { "same column id used more than once: $collisions"}
    }
    private val columnIdMap = columns.associateBy { it.id }

    fun addColumn(column: Column<K, *>): Columns<K> {
        return copy(columns = columns + column)
    }

    fun addIndex(entry: K): Columns<K> {
        require(!index.contains(entry)) { "index already set: $entry"}
        return copy(index = index + entry)
    }

    fun <V: Any> add(columnId: ColumnId<K, V>, key: K, value: V?): Columns<K> {
        require(columnIdMap.containsKey(columnId)) { "column $columnId not found" }
        return copy(columns = columns.map {
            if (it.id == columnId) {
                (it as Column<K, V>).add(key, value)
            } else it
        })
    }
}