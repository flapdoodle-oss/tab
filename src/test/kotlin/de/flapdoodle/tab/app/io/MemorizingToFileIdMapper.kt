package de.flapdoodle.tab.app.io

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.io.adapter.ToFileIdMapper

class MemorizingToFileIdMapper(val delegate: ToFileIdMapper = ToFileIdMapper.justCounting()): ToFileIdMapper {
    private var map = emptyMap<String, Id<out Any>>()
    override fun idFor(id: Id<out Any>): String {
        val mapped = delegate.idFor(id)
        map = map + (mapped to id)
        return mapped
    }

    fun toModelIdMapper() = ToModelIdFromMap(map)
}