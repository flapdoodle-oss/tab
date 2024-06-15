package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileSource
import de.flapdoodle.tab.model.connections.Source

interface SourceMapper {
    fun toFile(toFileMapping: ToFileMapping, src: Source): FileSource
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: TypeInfo<K>, src: FileSource): Source
}