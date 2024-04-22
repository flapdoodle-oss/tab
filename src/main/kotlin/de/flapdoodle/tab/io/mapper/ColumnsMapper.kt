package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileColumns
import de.flapdoodle.tab.model.data.Columns
import kotlin.reflect.KClass

interface ColumnsMapper {
    fun toFile(toFileMapping: ToFileMapping, src: Columns<out Comparable<*>>): FileColumns
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: KClass<K>, src: FileColumns): Columns<K>
}