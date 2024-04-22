package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileSource
import de.flapdoodle.tab.model.connections.Source
import kotlin.reflect.KClass

interface SourceMapper {
    fun toFile(toFileMapping: ToFileMapping, src: Source): FileSource
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: KClass<K>, src: FileSource): Source
}