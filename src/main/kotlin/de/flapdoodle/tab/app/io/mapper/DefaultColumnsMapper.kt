package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileColumns
import de.flapdoodle.tab.app.model.data.Columns
import kotlin.reflect.KClass

class DefaultColumnsMapper(
    private val columnMapper: ColumnMapper = DefaultColumnMapper
) : ColumnsMapper {
    override fun toFile(toFileMapping: ToFileMapping, src: Columns<out Comparable<*>>): FileColumns {
        return FileColumns(src.columns().map { columnMapper.toFile(toFileMapping, it) })
    }

    override fun <K : Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: KClass<K>, src: FileColumns): Columns<K> {
        return Columns(src.values.map { columnMapper.toModel(toModelMapping, indexType, it) })
    }
}