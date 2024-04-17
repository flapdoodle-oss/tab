package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileSingleValue
import de.flapdoodle.tab.app.model.data.SingleValue
import kotlin.reflect.KClass

object DefaultSingleValueMapper : Mapper<SingleValue<out Any>, FileSingleValue> {
    override fun toFile(toFileMapping: ToFileMapping, src: SingleValue<out Any>): FileSingleValue {
        return typedToFile(toFileMapping, src)
    }

    private fun <T: Any> typedToFile(toFileMapping: ToFileMapping, src: SingleValue<T>): FileSingleValue {
        return FileSingleValue(
            name = src.name,
            valueType = toFileMapping.valueType(src.valueType),
            value = if (src.value!=null) toFileMapping.value(src.valueType, src.value) else null,
            id = toFileMapping.idFor(src.id),
            color = src.color
        )
    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileSingleValue): SingleValue<out Any> {
        return typedToModel(toModelMapping, src, toModelMapping.valueType(src.valueType))
    }

    private fun <T: Any> typedToModel(toModelMapping: ToModelMapping, src: FileSingleValue, valueType: KClass<T>): SingleValue<T> {
        return SingleValue(
            name = src.name,
            valueType = valueType,
            value = if (src.value!=null) toModelMapping.value(valueType, src.value) else null,
            id = toModelMapping.nextId(src.id),
            color = src.color
        )
    }
}