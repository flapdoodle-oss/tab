package de.flapdoodle.tab.data

data class Table(private val columns: List<Column<out Any>> = emptyList()) {
  fun size() = columns.map { it.size() }.max() ?: 0

  fun columnIds() = columns.map { it.id }

  fun rows(): List<Row> {
    return (0..size()).map { row ->
      Row(columns.map { it.id to it[row] }.toMap())
    }
  }

  data class Row(private val map: Map<ColumnId<*>, Any?>) {
    operator fun <T: Any> get(id: ColumnId<T>): T? {
      @Suppress("UNCHECKED_CAST")
      return map[id] as T?
    }
  }
}