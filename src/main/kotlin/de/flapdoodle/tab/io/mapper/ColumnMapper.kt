package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileColumn
import de.flapdoodle.tab.model.data.Column
import kotlin.reflect.KClass

interface ColumnMapper {
    fun toFile(toFileMapping: ToFileMapping, src: Column<out Comparable<*>, out Any>): FileColumn
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: TypeInfo<K>, src: FileColumn): Column<K, out Any>
}