package de.flapdoodle.tab.model

import kotlin.reflect.KClass

data class Table<K : Comparable<K>>(
    val type: KClass<K>,
    val id: Int = idGenerator.nextIdFor(Table::class),
    private val columnValues: Map<ColumnId<Any>, ColumnValues<K, Any>> = emptyMap()
) {
    companion object {
        val idGenerator = IdGenerator()
    }
}