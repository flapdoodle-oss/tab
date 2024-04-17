package de.flapdoodle.tab.app.io.adapter

import de.flapdoodle.kfx.types.Id
import kotlin.reflect.KClass

class ToModelMapping(
    val indexTypeMapper: IndexTypeMapper = KeyMapIndexTypeMapper.defaultMapper(),
    val typeMapper: TypeMapper = KeyMapTypeMapper.defaultMapper(),
    val valueMapper: ValueMapper = KeyMapValueMapper.defaultMapper(),
    val toModelIdMapper: ToModelIdMapper = ToModelIdMapper.justDelegate(),
) {
    fun <T : Any> nextId(id: String, type: KClass<T>): Id<T> = toModelIdMapper.nextId(id, type)

    fun valueType(valueType: String) = typeMapper.toModel(valueType)
    fun <T: Any> value(valueType: KClass<T>, value: String) = valueMapper.toModel(valueType, value)

    fun indexType(valueType: String) = indexTypeMapper.toModel(valueType)
}