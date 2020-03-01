package de.flapdoodle.tab.data

import de.flapdoodle.tab.data.values.Values
import kotlin.reflect.KClass

data class Data(
    private val columnValues: Map<ColumnId<out Any>, Values<out Any>> = emptyMap()
) {

  operator fun <T: Any> get(columnId: ColumnId<out T>): Values<T> {
    @Suppress("UNCHECKED_CAST")
    return columnValues[columnId] as Values<T>?
        ?: Values(columnId.type as KClass<T>)
  }

  fun <T : Any> change(id: ColumnId<out T>, row: Int, value: T?): Data {
    return copy(columnValues = columnValues + (id to get(id).set(row, value)))
  }

  fun clear(id: ColumnId<out Any>): Data {
    return copy(columnValues = columnValues + (id to Values(id.type)))
  }


  fun rows(columns: List<ColumnId<out Any>>): List<Row> {
    val columnValues = columns.map { it to get(it) }
    val size = columnValues.map { it.second.size() }.max() ?: 0

    return (0 until size).mapIndexed { idx, row ->
      Row(idx, columnValues.map { it.first to it.second[row] }.toMap())
    }
  }

  data class Row(val index: Int, private val map: Map<ColumnId<out Any>, Any?>) {
    operator fun <T : Any> get(id: ColumnId<T>): T? {
      @Suppress("UNCHECKED_CAST")
      return map[id] as T?
    }
  }
}