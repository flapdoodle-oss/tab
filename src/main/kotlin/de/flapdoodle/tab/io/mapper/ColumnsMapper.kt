package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileColumns
import de.flapdoodle.tab.model.data.Columns

interface ColumnsMapper {
    fun toFile(toFileMapping: ToFileMapping, src: Columns<out Comparable<*>>): FileColumns
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: TypeInfo<K>, src: FileColumns): Columns<K>
}