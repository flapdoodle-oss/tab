package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.ColumnId

sealed class ColumnConnection<T: Any> {
  abstract val columnId: ColumnId<T>

  data class ColumnValues<T : Any>(override val columnId: ColumnId<T>) : ColumnConnection<T>()
  data class Aggregate<T : Any>(override val columnId: ColumnId<T>) : ColumnConnection<T>()
}