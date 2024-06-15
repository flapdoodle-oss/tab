package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.data.Column

data class AddColumn<K : Comparable<K>>(
    val id: Id<out Node.Table<out Comparable<*>>>,
    val column: Column<K, out Any>
) : TableModifier(
    id = id,
    change = { table ->
        require(table.indexType == column.indexType) { "type mismatch: $column" }
        val matchingTable = table as Node.Table<K>
        matchingTable.copy(columns = matchingTable.columns.addColumn(column))
    }
)