package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.data.ColumnId

interface ColumnValueChangeListener {
  fun <T : Any> change(id: ColumnId<out T>, row: Int, value: T?)
}
