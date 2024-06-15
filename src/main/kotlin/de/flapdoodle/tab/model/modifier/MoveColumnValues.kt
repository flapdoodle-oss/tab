package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.changes.Change
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.Columns

data class MoveColumnValues<K: Comparable<K>>(
    val id: Id<out Node.Table<out Comparable<*>>>,
    val lastIndex: K,
    val index: K
) : TableModifier(
    id = id,
    change = { table ->
        val typeTable = table as Node.Table<K>
        typeTable.copy(columns = typeTable.columns.moveValues(lastIndex, index))
    }
) {
    companion object {
        fun <K: Comparable<K>> asModifier(change: Change.Table.MoveValues<K>): MoveColumnValues<K> {
            return MoveColumnValues(change.id, change.lastIndex, change.index)
        }
    }
}