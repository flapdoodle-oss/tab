package de.flapdoodle.tab.data

data class Table(private val columns: List<Column<out Any>> = emptyList()) {
  fun size() = columns.map { it.size() }.max() ?: 0

  fun columnIds() = columns.map { it.id }

  fun add(column: Column<*>): Table {
    require(!columns.any { it.id == column.id }) { "column with id ${column.id} already set" }
    return copy(columns = columns + column)
  }

  fun <T : Any> change(id: ColumnId<T>, row: Int, value: T?): Table {
    return copy(columns = columns.map {
      change(it, id, row, value)
    })
  }

  private fun <T : Any> change(column: Column<*>, id: ColumnId<T>, row: Int, value: T?): Column<*> {
    return if (column.id == id) {
      @Suppress("UNCHECKED_CAST")
      (column as Column<T>).set(row, value)
    } else {
      column
    }
  }

  fun rows(): List<Row> {
    return (0 until size()).mapIndexed { idx, row ->
      Row(idx, columns.map { it.id to it[row] }.toMap())
    }
  }

  data class Row(val index: Int, private val map: Map<ColumnId<*>, Any?>) {
    operator fun <T : Any> get(id: ColumnId<T>): T? {
      @Suppress("UNCHECKED_CAST")
      return map[id] as T?
    }
  }
}