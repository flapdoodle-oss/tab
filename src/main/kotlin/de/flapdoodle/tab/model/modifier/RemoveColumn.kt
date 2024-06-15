package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.ColumnId

data class RemoveColumn(
    val id: Id<out Node.Table<out Comparable<*>>>,
    val columnId: ColumnId
) : TableModifier(
    id = id,
    change = { table -> removeColumn(table, columnId) }
) {
    companion object {
        private fun <K: Comparable<K>> removeColumn(table: Node.Table<K>, columnId: ColumnId): Node.Table<K> {
            return table.copy(columns = table.columns.remove(columnId))
        }
    }
}