package de.flapdoodle.tab.app.model.data

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.kfx.types.Key
import kotlin.reflect.KClass

data class ColumnId(
    val id: Id<ColumnId> = Id.nextId(ColumnId::class)
): DataId()