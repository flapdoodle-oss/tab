package de.flapdoodle.tab.app.model.data

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.kfx.types.Key
import kotlin.reflect.KClass

data class SingleValueId(
    val id: Id<SingleValueId> = Id.nextId(SingleValueId::class)
): DataId()