package de.flapdoodle.tab.data

import de.flapdoodle.tab.data.values.Values
import kotlin.reflect.KClass

data class Data(
    val columnValues: Map<ColumnId<out Any>, Values<out Any>> = emptyMap(),
    val singleValueColumns: Set<ColumnId<out Any>> = emptySet()
) {


  operator fun <T: Any> get(columnId: ColumnId<out T>): Values<T> {
    return getOrCreate(columnId)
  }

  private fun <T : Any> getOrCreate(columnId: ColumnId<out T>): Values<T> {
    @Suppress("UNCHECKED_CAST")
    return columnValues[columnId] as Values<T>?
        ?: Values(columnId.type as KClass<T>)
  }

  fun <T : Any> change(id: ColumnId<out T>, row: Int, value: T?): Data {
    return copy(
        columnValues = columnValues + (id to getOrCreate(id).set(row, value)),
        singleValueColumns = singleValueColumns - id
    )
  }

  fun <T: Any> change(id: ColumnId<out T>, value: T?): Data {
    return copy(
        columnValues = columnValues + (id to getOrCreate(id).clear().set(0, value)),
        singleValueColumns = singleValueColumns + id
    )
  }

  fun isSingleValue(id: ColumnId<out Any>): Boolean = singleValueColumns.contains(id)

  fun clear(id: ColumnId<out Any>): Data {
    return copy(columnValues = columnValues - id)
  }


  fun rows(columns: List<ColumnId<out Any>>): List<Row> {
    val columnValues = columns.map { it to get(it) }
    val size = columnValues.map { it.second.size() }.max() ?: 0

    return (0 until size).mapIndexed { idx, row ->
      Row(idx, columnValues.map { it.first to it.second[row] }.toMap())
    }
  }

  fun explain() {
    columnValues.forEach { columnId, values ->
      println("column $columnId -> $values")
    }
  }

  fun filterUnused(columnIds: Set<ColumnId<out Any>>): Data {
    if (!columnIds.containsAll(columnValues.keys)) {
      return copy(columnValues = columnValues.filterKeys { columnIds.contains(it) })
    } else
    return this
  }

  data class Row(val index: Int, private val map: Map<ColumnId<out Any>, Any?>) {
    operator fun <T : Any> get(id: ColumnId<T>): T? {
      @Suppress("UNCHECKED_CAST")
      return map[id] as T?
    }
  }
}