package de.flapdoodle.tab.app.model.data

data class Columns<K: Comparable<K>>(
    val columns: List<Column<K, *>> = emptyList()
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

    fun <V : Any> column(columnId: ColumnId<K, V>): Column<K, V> {
        return requireNotNull(columnIdMap[columnId]) { "column $columnId not found" } as Column<K, V>
    }

    fun <V: Any> add(columnId: ColumnId<K, V>, key: K, value: V?): Columns<K> {
        val column = column(columnId).add(key, value)
        return copy(columns = columns.map {
            if (it.id == column.id) column else it
        })
    }

    fun <V: Any> get(columnId: ColumnId<K, V>, key: K): V? {
        return column(columnId)[key]
    }
}