package de.flapdoodle.tab.io.adapter

import de.flapdoodle.kfx.types.Id
import kotlin.reflect.KClass

class ToFileMapping(
    val indexTypeMapper: IndexTypeMapper = KeyMapIndexTypeMapper.defaultMapper(),
    val typeMapper: TypeMapper = KeyMapTypeMapper.defaultMapper(),
    val valueMapper: ValueMapper = KeyMapValueMapper.defaultMapper(),
    val toFileIdMapper: ToFileIdMapper = ToFileIdMapper.justCounting(),
) {
    fun idFor(id: Id<out Any>) = toFileIdMapper.idFor(id)

    fun valueType(valueType: KClass<out Any>) = typeMapper.toFile(valueType)
    fun indexType(valueType: KClass<out Any>) = indexTypeMapper.toFile(valueType)

    fun <T: Any> value(valueType: KClass<T>, value: T) = valueMapper.toFile(valueType, value)
}