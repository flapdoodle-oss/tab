package de.flapdoodle.tab.io

import de.flapdoodle.tab.io.adapter.*

data class MemorizingMapping(
    val indexTypeMapper: IndexTypeMapper = KeyMapIndexTypeMapper.defaultMapper(),
    val toFileIdMapper: MemorizingToFileIdMapper = MemorizingToFileIdMapper(),
) {
    fun toFileMapping() = ToFileMapping(
        indexTypeMapper = indexTypeMapper,
        toFileIdMapper = toFileIdMapper
    )

    fun toModelMapping() = ToModelMapping(
        indexTypeMapper = indexTypeMapper,
        toModelIdMapper = toFileIdMapper.toModelIdMapper()
    )
}