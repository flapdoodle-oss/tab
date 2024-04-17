package de.flapdoodle.tab.app.io

import de.flapdoodle.tab.app.io.adapter.*

data class MemorizingMapping(
    val indexTypeMapper: IndexTypeMapper = KeyMapIndexTypeMapper.defaultMapper(),
    val toFileIdMapper: MemorizingToFileIdMapper = MemorizingToFileIdMapper(),
    val toFileDataIdMapper: MemorizingToFileDataIdMapper = MemorizingToFileDataIdMapper(ToFileDataIdMapper.justCounting(indexTypeMapper))
) {
    fun toFileMapping() = ToFileMapping(
        indexTypeMapper = indexTypeMapper,
        toFileIdMapper = toFileIdMapper,
        toFileDataIdMapper = toFileDataIdMapper
    )

    fun toModelMapping() = ToModelMapping(
        indexTypeMapper = indexTypeMapper,
        toModelIdMapper = toFileIdMapper.toModelIdMapper(),
        toModelDataIdMapper = toFileDataIdMapper.toModelDataIdMapper()
    )
}