package de.flapdoodle.tab.graph.nodes.connections

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.graph.events.IsMarker

sealed class Out : IsMarker {
  data class ColumnValues<T : Any>(val columnId: ColumnId<T>) : Out()
  data class Aggregate<T : Any>(val columnId: ColumnId<T>) : Out()
}