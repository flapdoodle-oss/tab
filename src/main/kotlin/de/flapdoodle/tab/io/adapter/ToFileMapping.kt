package de.flapdoodle.tab.io.adapter

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo

class ToFileMapping(
    val indexTypeMapper: IndexTypeMapper = KeyMapIndexTypeMapper.defaultMapper(),
    val typeMapper: TypeMapper = KeyMapTypeMapper.defaultMapper(),
    val valueMapper: ValueMapper = KeyMapValueMapper.defaultMapper(),
    val toFileIdMapper: ToFileIdMapper = ToFileIdMapper.justCounting(),
) {
    fun idFor(id: Id<out Any>) = toFileIdMapper.idFor(id)

    fun valueType(valueType: TypeInfo<out Any>) = typeMapper.toFile(valueType)
    fun indexType(valueType: TypeInfo<out Any>) = indexTypeMapper.toFile(valueType)

    fun <T: Any> value(valueType: TypeInfo<T>, value: T) = valueMapper.toFile(valueType, value)
}