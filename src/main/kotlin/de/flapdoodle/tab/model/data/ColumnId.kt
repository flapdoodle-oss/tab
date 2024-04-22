package de.flapdoodle.tab.model.data

import de.flapdoodle.kfx.types.Id

data class ColumnId(
    val id: Id<ColumnId> = Id.nextId(ColumnId::class)
): DataId()