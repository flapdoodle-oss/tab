package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.changes.Change

data class RemoveColumnValues<K: Comparable<K>>(
    val id: Id<out Node.Table<out Comparable<*>>>,
    val index: K
) : TableModifier(
    id = id,
    change = { table ->
        val typeTable = table as Node.Table<K>
        typeTable.copy(columns = typeTable.columns.removeValues(index))
    }
) {
    companion object {
        fun <K: Comparable<K>> asModifier(change: Change.Table.RemoveValues<K>): RemoveColumnValues<K> {
            return RemoveColumnValues(change.id, change.index)
        }
    }
}