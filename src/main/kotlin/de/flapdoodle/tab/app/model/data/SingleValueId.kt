package de.flapdoodle.tab.app.model.data

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.kfx.types.Key
import kotlin.reflect.KClass

data class SingleValueId(
//    val valueType: KClass<V>,
    val id: Id<SingleValueId> = Id.Companion.nextId(SingleValueId::class)
): DataId()