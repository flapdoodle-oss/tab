package de.flapdoodle.tab.app.io

import de.flapdoodle.tab.app.io.adapter.ToModelDataIdMapper
import de.flapdoodle.tab.app.io.file.FileDataId
import de.flapdoodle.tab.app.model.data.DataId

class ToModelDataIdFromMap(val map: Map<FileDataId, DataId>) : ToModelDataIdMapper {
    override fun nextId(id: FileDataId): DataId {
        return requireNotNull(map[id]) { "$id not found in $map" } as DataId
    }
}