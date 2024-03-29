package de.flapdoodle.tab.data.values

import kotlin.reflect.KClass

data class ValueContainer<T: Any>(
    val type: KClass<T>,
    private val rows: Map<Int, T> = emptyMap()
) {
  fun size(): Int {
    return rows.keys.maxOrNull()?.let { it + 1 } ?: 0
  }

  operator fun set(index: Int, value: T?): ValueContainer<T> {
    return copy(rows = if (value != null) rows + (index to value) else rows - index)
  }

  operator fun get(index: Int): T? {
    return rows[index]
  }

  fun clear(): ValueContainer<T> {
    return copy(rows = emptyMap())
  }

  fun asList(): List<T?> {
    return (0 until size()).map { get(it) }
  }
}