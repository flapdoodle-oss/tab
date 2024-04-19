package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileDataId
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.DataId
import de.flapdoodle.tab.app.model.data.SingleValueId

class DefaultDataIdMapper : Mapper<DataId, FileDataId> {
    override fun toFile(toFileMapping: ToFileMapping, src: DataId): FileDataId {
        return when (src) {
            is SingleValueId -> FileDataId(valueId = toFileMapping.idFor(src.id))
            is ColumnId -> FileDataId(columnId = toFileMapping.idFor(src.id))
        }
    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileDataId): DataId {
        return if (src.valueId!=null) {
            SingleValueId(toModelMapping.nextId(src.valueId, SingleValueId::class))
        } else {
            ColumnId(toModelMapping.nextId(src.columnId!!, ColumnId::class))
        }
    }
}