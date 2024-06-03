package de.flapdoodle.tab.io.mapper

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileColor
import de.flapdoodle.tab.io.file.FileSingleValue
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.model.data.SingleValueId
import kotlin.reflect.KClass

object DefaultSingleValueMapper : Mapper<SingleValue<out Any>, FileSingleValue> {
    override fun toFile(toFileMapping: ToFileMapping, src: SingleValue<out Any>): FileSingleValue {
        return typedToFile(toFileMapping, src)
    }

    private fun <T: Any> typedToFile(toFileMapping: ToFileMapping, src: SingleValue<T>): FileSingleValue {
        return FileSingleValue(
            name = src.name.long,
            short = src.name.short,
            valueType = toFileMapping.valueType(src.valueType),
            value = if (src.value!=null) toFileMapping.value(src.valueType, src.value) else null,
            id = toFileMapping.idFor(src.id.id),
            color = FileColor.from(src.color)
        )
    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileSingleValue): SingleValue<out Any> {
        return typedToModel(toModelMapping, src, toModelMapping.valueType(src.valueType))
    }

    private fun <T: Any> typedToModel(toModelMapping: ToModelMapping, src: FileSingleValue, valueType: TypeInfo<T>): SingleValue<T> {
        return SingleValue(
            name = Name(src.name, src.short),
            valueType = valueType,
            value = if (src.value!=null) toModelMapping.value(valueType, src.value) else null,
            id = SingleValueId(toModelMapping.nextId(src.id, SingleValueId::class)),
            color = src.color.toColor()
        )
    }
}