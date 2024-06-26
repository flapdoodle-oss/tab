package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileColumns
import de.flapdoodle.tab.model.data.Columns

class DefaultColumnsMapper(
    private val columnMapper: ColumnMapper = DefaultColumnMapper
) : ColumnsMapper {
    override fun toFile(toFileMapping: ToFileMapping, src: Columns<out Comparable<*>>): FileColumns {
        return FileColumns(src.columns().map { columnMapper.toFile(toFileMapping, it) })
    }

    override fun <K : Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: TypeInfo<K>, src: FileColumns): Columns<K> {
        return Columns(src.values.map { columnMapper.toModel(toModelMapping, indexType, it) })
    }
}