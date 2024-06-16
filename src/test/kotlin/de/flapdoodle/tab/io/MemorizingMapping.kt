package de.flapdoodle.tab.io

import de.flapdoodle.tab.io.adapter.IndexTypeMapper
import de.flapdoodle.tab.io.adapter.KeyMapIndexTypeMapper
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping

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