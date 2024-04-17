package de.flapdoodle.tab.app.io

import de.flapdoodle.tab.app.io.adapter.*

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