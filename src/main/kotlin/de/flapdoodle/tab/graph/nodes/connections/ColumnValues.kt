package de.flapdoodle.tab.graph.nodes.connections

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.graph.events.IsMarker

data class ColumnValues<T : Any>(val columnId: ColumnId<T>) : Out