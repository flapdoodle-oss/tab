package de.flapdoodle.tab.model.change

import de.flapdoodle.tab.model.changes.Change

object ModelChange2ChangeTranslator {
    fun asChange(change: ModelChange): Change? {
        return when (change) {
            is ModelChange.ChangeTableProperties -> Change.Table.Properties(change.id, change.name)
            is ModelChange.AddColumn<out Comparable<*>> -> Change.Table.AddColumn(change.id, change.column)
            is ModelChange.ChangeColumnProperties<out Comparable<*>> -> Change.Table.ColumnProperties(
                change.id,
                change.columnId,
                change.name,
                change.interpolationType
            )
            is ModelChange.SetColumns<out Comparable<*>> -> asSetColumns(change)
            is ModelChange.RemoveColumn -> Change.Table.RemoveColumn(change.id, change.columnId)
            else -> null
        }
    }

    private fun <K: Comparable<K>> asSetColumns(change: ModelChange.SetColumns<K>) =
        Change.Table.SetColumns(change.id, change.index, change.changes)
}