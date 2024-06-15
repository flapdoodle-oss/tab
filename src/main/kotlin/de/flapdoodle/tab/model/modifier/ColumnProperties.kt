package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.data.ColumnId

data class ColumnProperties(
    val id: Id<out Node.Table<out Comparable<*>>>,
    val columnId: ColumnId,
    val name: Name,
    val interpolationType: InterpolationType
) : TableModifier(
    id = id,
    change = { table ->
        columnProperties(table, columnId, name, interpolationType)
    }
) {
    companion object {
        private fun <K: Comparable<K>> columnProperties(
            table: Node.Table<K>,
            columnId: ColumnId,
            name: Name,
            interpolationType: InterpolationType
        ): Node.Table<K> {
            return table.copy(columns = table.columns.change(columnId) { it.copy(name = name, interpolationType = interpolationType) })
        }
    }
}