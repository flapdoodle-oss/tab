package de.flapdoodle.tab.app.io

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.io.adapter.ToModelIdMapper
import kotlin.reflect.KClass

class ToModelIdFromMap(val map: Map<String, Id<out Any>>): ToModelIdMapper {
    override fun <T : Any> nextId(id: String, type: KClass<T>): Id<T> {
        return requireNotNull(map[id]) { "$id not found in $map"} as Id<T>
    }
}