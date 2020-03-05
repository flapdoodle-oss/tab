package de.flapdoodle.tab.graph.nodes.connections

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.graph.events.IsMarker

sealed class Out<T: Any> : IsMarker {
  abstract val columnId: ColumnId<T>
  data class ColumnValues<T : Any>(override val columnId: ColumnId<T>) : Out<T>()
  data class Aggregate<T : Any>(override val columnId: ColumnId<T>) : Out<T>()
}