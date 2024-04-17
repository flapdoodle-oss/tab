package de.flapdoodle.tab.app.io.adapter

import de.flapdoodle.tab.app.io.file.FileDataId
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.DataId
import de.flapdoodle.tab.app.model.data.SingleValueId

interface ToFileDataIdMapper {
    fun idFor(id: DataId): FileDataId

    companion object {
        fun justCounting(indexTypeMapper: IndexTypeMapper) = JustCounting(indexTypeMapper)

        class JustCounting(
            val indexTypeMapper: IndexTypeMapper
        ) : ToFileDataIdMapper {
            private var counter: Int = 1
            private var map = emptyMap<DataId, Int>()

            override fun idFor(id: DataId): FileDataId {
                var mapped = map[id]
                if (mapped==null) {
                    mapped = counter++
                    map = map + (id to mapped)
                }

                return when (id) {
                    is SingleValueId -> {
                        FileDataId.SingleValueId(mapped)
                    }
                    is ColumnId<out Comparable<*>> -> {
                        FileDataId.ColumnId(
                            mapped,
                            indexTypeMapper.toFile(id.indexType)
                        )
                    }
                }
            }

        }
    }

}