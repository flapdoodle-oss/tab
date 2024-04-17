package de.flapdoodle.tab.app.io.adapter

import de.flapdoodle.tab.app.io.file.FileDataId
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.DataId
import de.flapdoodle.tab.app.model.data.SingleValueId

interface ToModelDataIdMapper {
    fun nextId(id: FileDataId): DataId

    companion object {
        fun justDelegate(indexTypeMapper: IndexTypeMapper) = JustDelegate(indexTypeMapper)

        class JustDelegate(
            val indexTypeMapper: IndexTypeMapper
        ) : ToModelDataIdMapper {

            override fun nextId(id: FileDataId): DataId {
                return when(id) {
                    is FileDataId.SingleValueId -> {
                        SingleValueId()
                    }
                    is FileDataId.ColumnId -> {
                        ColumnId(indexTypeMapper.toModel(id.indexType))
                    }
                }
            }

        }
    }

}