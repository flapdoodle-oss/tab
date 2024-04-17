package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileColumn
import de.flapdoodle.tab.app.model.data.Column
import de.flapdoodle.tab.app.model.data.ColumnId
import kotlin.reflect.KClass

object DefaultColumnMapper : ColumnMapper {
    override fun toFile(toFileMapping: ToFileMapping, src: Column<out Comparable<*>, out Any>): FileColumn {
        return internalToFile(toFileMapping, src)
    }

    private fun <K: Comparable<K>, V: Any> internalToFile(toFileMapping: ToFileMapping, src: Column<K, V>): FileColumn {
        return FileColumn(
            name = src.name,
            valueType = toFileMapping.valueType(src.valueType),
            id = toFileMapping.idFor(src.id),
            color = src.color,
            values = src.values.map { (key, value) ->
                toFileMapping.value(src.indexType, key) to toFileMapping.value(src.valueType, value)
            }.toMap()
        )
    }

    override fun <K: Comparable<K>> toModel(toModelMapping: ToModelMapping, indexType: KClass<K>, src: FileColumn): Column<K, out Any> {
        return toModel(toModelMapping, src, indexType, toModelMapping.valueType(src.valueType))
    }

    private fun <K: Comparable<K>, V: Any> toModel(
        toModelMapping: ToModelMapping,
        src: FileColumn,
        indexType: KClass<K>,
        valueType: KClass<V>
    ): Column<K, V> {
        val id = toModelMapping.nextId(src.id)
        require(id.indexType == indexType) {"type mismatch: $indexType != ${id.indexType}"}
        return Column(
            name = src.name,
            indexType = indexType,
            valueType = valueType,
            id = id as ColumnId<K>,
            color = src.color,
            values = src.values.map { (key, value) ->
                toModelMapping.value(indexType, key) to toModelMapping.value(valueType, value)
            }.toMap()
        )
    }
}