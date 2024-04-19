package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileSource
import de.flapdoodle.tab.app.model.connections.Source
import kotlin.reflect.KClass

interface SourceMapper {
    fun toFile(toFileMapping: ToFileMapping, src: Source): FileSource
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: KClass<K>, src: FileSource): Source
}