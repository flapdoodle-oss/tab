package de.flapdoodle.tab.model.change

import de.flapdoodle.tab.model.changes.Change

object ModelChange2ChangeTranslator {
    fun asChange(modelChange: ModelChange): Change? {
        return when (modelChange) {
            is ModelChange.AddColumn<out Comparable<*>> -> Change.Table.AddColumn(modelChange.id, modelChange.column)
            is ModelChange.RemoveColumn -> Change.Table.RemoveColumn(modelChange.id, modelChange.columnId)
            else -> null
        }
    }
}