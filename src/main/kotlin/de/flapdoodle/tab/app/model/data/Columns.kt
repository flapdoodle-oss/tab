package de.flapdoodle.tab.app.model.data

import kotlin.reflect.KClass

data class Columns<K: Comparable<K>>(
    private val columns: List<Column<K, out Any>> = emptyList()
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
    fun columns() = columns

    fun addColumn(column: Column<K, out Any>): Columns<K> {
        return copy(columns = columns + column)
    }

    fun remove(id: ColumnId): Columns<K> {
        return copy(columns = columns.filter { it.id != id })
    }

    fun column(columnId: ColumnId): Column<K, out Any> {
        return requireNotNull(columnIdMap[columnId]) { "column $columnId not found" }
    }

    fun find(id: ColumnId): Column<K, out Any>? {
        return columnIdMap[id]
    }

    fun <V: Any> add(columnId: ColumnId, key: K, valueType: KClass<V>, value: V?): Columns<K> {
        val c: Column<K, out Any> = column(columnId)
        require(c.valueType == valueType) {"value type mismatch: $valueType != ${c.valueType}"}
        val column = (c as Column<K, V>).add(key, value)
        return copy(columns = columns.map {
            if (it.id == column.id) column else it
        })
    }

    fun change(id: ColumnId, map: (Column<K, out Any>) -> Column<K, out Any>): Columns<K> {
        return copy(columns = columns.map { if (it.id==id) map(it) else it })
    }

    fun set(id: ColumnId, key: K, value: Any?): Columns<K> {
        val c: Column<K, out Any> = column(id)
        require( value == null || c.valueType.isInstance(value)) {"value type mismatch: $value != ${c.valueType}"}
        val column = (c as Column<K, Any>).add(key, value)
        return copy(columns = columns.map {
            if (it.id == column.id) column else it
        })
    }

    fun moveValues(lastIndex: K, newIndex: K): Columns<K> {
        require(!index.contains(newIndex)) { "can not move values to existing index: $index" }

        return copy(columns = columns.map { it.moveValue(lastIndex, newIndex) })
    }

    fun get(columnId: ColumnId, key: K): Any? {
        return column(columnId)[key]
    }

    fun forEach(action: (Column<K, out Any>) -> Unit) {
        columns.forEach(action)
    }

    fun filter(predicate: (Column<K, out Any>) -> Boolean): Columns<K> {
        return Columns(columns.filter(predicate))
    }
}