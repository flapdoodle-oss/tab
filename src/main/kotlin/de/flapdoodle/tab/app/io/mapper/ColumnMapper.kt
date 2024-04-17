package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileColumn
import de.flapdoodle.tab.app.model.data.Column
import kotlin.reflect.KClass

interface ColumnMapper {
    fun toFile(toFileMapping: ToFileMapping, src: Column<out Comparable<*>, out Any>): FileColumn
    fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: KClass<K>, src: FileColumn): Column<K, out Any>
}