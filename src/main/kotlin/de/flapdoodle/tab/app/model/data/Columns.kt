package de.flapdoodle.tab.app.model.data

import kotlin.reflect.KClass

data class Columns<K: Comparable<K>>(
    val columns: List<Column<K, out Any>> = emptyList()
) {
    init {
        val collisions = columns.groupBy { it.id }
            .filter { it.value.size > 1 }
            .keys
        require(collisions.isEmpty()) { "same column id used more than once: $collisions"}
    }
    private val columnIdMap by lazy { columns.associateBy { it.id } }
    private val index by lazy { columns.flatMap { it.values.keys }.toSortedSet() }

    fun index() = index

    fun addColumn(column: Column<K, *>): Columns<K> {
        return copy(columns = columns + column)
    }

    fun column(columnId: ColumnId): Column<K, out Any> {
        return requireNotNull(columnIdMap[columnId]) { "column $columnId not found" }
    }

    fun <V: Any> add(columnId: ColumnId, key: K, valueType: KClass<V>, value: V?): Columns<K> {
        val c: Column<K, out Any> = column(columnId)
        require(c.valueType == valueType) {"value type mismatch: $valueType != ${c.valueType}"}
        val column = (c as Column<K, V>).add(key, value)
        return copy(columns = columns.map {
            if (it.id == column.id) column else it
        })
    }

    fun get(columnId: ColumnId, key: K): Any? {
        return column(columnId)[key]
    }

    fun forEach(action: (Column<K, out Any>) -> Unit) {
        columns.forEach(action)
    }
}