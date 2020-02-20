package de.flapdoodle.tab.data

import kotlin.reflect.KClass

data class Column<T : Any>(val id: ColumnId<T>, private val rows: Map<Int, T> = emptyMap()) {

  fun size(): Int {
    return rows.keys.max()?.let { it + 1 } ?: 0
  }

  operator fun set(index: Int, value: T?): Column<T> {
    return copy(rows = if (value != null) rows + (index to value) else rows - index)
  }

  operator fun get(index: Int): T? {
    return rows[index]
  }
}