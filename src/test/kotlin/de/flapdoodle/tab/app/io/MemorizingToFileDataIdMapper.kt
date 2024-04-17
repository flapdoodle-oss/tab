package de.flapdoodle.tab.app.io

import de.flapdoodle.tab.app.io.adapter.ToFileDataIdMapper
import de.flapdoodle.tab.app.io.file.FileDataId
import de.flapdoodle.tab.app.model.data.DataId

class MemorizingToFileDataIdMapper(val delegate: ToFileDataIdMapper) : ToFileDataIdMapper {
    private var map = emptyMap<FileDataId, DataId>()
    override fun idFor(id: DataId): FileDataId {
        val mapped = delegate.idFor(id)
        map = map + (mapped to id)
        return mapped
    }

    fun toModelDataIdMapper() = ToModelDataIdFromMap(map)
}