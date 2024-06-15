package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.Columns

data class SetColumnValues<K: Comparable<K>>(
    val id: Id<out Node.Table<out Comparable<*>>>,
    val index: K,
    val changes: List<Pair<ColumnId, Any?>>
) : TableModifier(
    id = id,
    change = { table ->
        columnProperties(table as Node.Table<K>, index, changes)
    }
) {
    companion object {
        private fun <K: Comparable<K>> columnProperties(
            table: Node.Table<K>,
            index: K,
            changes: List<Pair<ColumnId, Any?>>
        ): Node.Table<K> {
            return table.copy(columns = changes.fold(table.columns) { c: Columns<K>, idAndValue: Pair<ColumnId, Any?> ->
                c.set(idAndValue.first, index, idAndValue.second)
            })
        }

        fun <K: Comparable<K>> asModifier(change: Change.Table.SetColumns<K>): SetColumnValues<K> {
            return SetColumnValues(change.id, change.index, change.changes)
        }
    }
}