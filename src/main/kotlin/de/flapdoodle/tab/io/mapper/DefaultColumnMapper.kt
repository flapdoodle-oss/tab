package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileColor
import de.flapdoodle.tab.io.file.FileColumn
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.ColumnId

object DefaultColumnMapper : ColumnMapper {
    override fun toFile(toFileMapping: ToFileMapping, src: Column<out Comparable<*>, out Any>): FileColumn {
        return internalToFile(toFileMapping, src)
    }

    private fun <K: Comparable<K>, V: Any> internalToFile(toFileMapping: ToFileMapping, src: Column<K, V>): FileColumn {
        return FileColumn(
            name = src.name.long,
            short = src.name.short,
            valueType = toFileMapping.valueType(src.valueType),
            id = toFileMapping.idFor(src.id.id),
            color = FileColor.from(src.color),
            values = src.values.map { (key, value) ->
                toFileMapping.value(src.indexType, key) to toFileMapping.value(src.valueType, value)
            }.toMap()
        )
    }

    override fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: TypeInfo<K>, src: FileColumn): Column<K, out Any> {
        return toModel(toModelMapping, src, indexType, toModelMapping.valueType(src.valueType))
    }

    private fun <K: Comparable<K>, V: Any> toModel(
        toModelMapping: ToModelMapping,
        src: FileColumn,
        indexType: TypeInfo<K>,
        valueType: TypeInfo<V>
    ): Column<K, V> {
        return Column(
            name = Name(src.name, src.short),
            indexType = indexType,
            valueType = valueType,
            id = ColumnId(toModelMapping.nextId(src.id, ColumnId::class)),
            color = src.color.toColor(),
            values = src.values.map { (key, value) ->
                toModelMapping.value(indexType, key) to toModelMapping.value(valueType, value)
            }.toMap()
        )
    }
}