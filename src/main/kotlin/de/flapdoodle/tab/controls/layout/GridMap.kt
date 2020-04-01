package de.flapdoodle.tab.controls.layout

data class GridMap<T : Any>(
    private val map: Map<Pos, T> = emptyMap()
) {

  private val maxColumn = map.keys.map { it.column }.max() ?: 0
  private val maxRow = map.keys.map { it.row }.max() ?: 0

  fun columns() = 0..maxColumn
  fun rows() = 0..maxRow
  fun values() = map.values

  fun <D : Any> mapColumns(allColumnRows: (Collection<T>) -> D): List<D> {
    return columns().map { column ->
      allColumnRows(map.filter { it.key.column == column }.values)
    }
  }

  fun <D : Any> mapRows(allRowColumns: (Collection<T>) -> D): List<D> {
    return rows().map { row ->
      allRowColumns(map.filter { it.key.row == row }.values)
    }
  }

  companion object {
    fun <T : Any> create(src: Collection<T>, posOf: (T) -> Pos): GridMap<T> {
      return GridMap(src.map { posOf(it) to it }.toMap())
    }
  }

  data class Pos(
      val column: Int,
      val row: Int
  ) {
    init {
      require(column >= 0) { "invalid column: $column" }
      require(row >= 0) { "invalid row: $row" }
    }
  }
}