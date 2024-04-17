package de.flapdoodle.tab.app.io.adapter

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.io.file.FileDataId
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId
import kotlin.reflect.KClass

class ToFileMapping(
    val indexTypeMapper: IndexTypeMapper = KeyMapIndexTypeMapper.defaultMapper(),
    val typeMapper: TypeMapper = KeyMapTypeMapper.defaultMapper(),
    val valueMapper: ValueMapper = KeyMapValueMapper.defaultMapper(),
    val toFileIdMapper: ToFileIdMapper = ToFileIdMapper.justCounting(),
    val toFileDataIdMapper: ToFileDataIdMapper = ToFileDataIdMapper.justCounting(indexTypeMapper),
) {
    fun idFor(id: Id<out Any>) = toFileIdMapper.idFor(id)
    fun idFor(id: SingleValueId) = toFileDataIdMapper.idFor(id) as FileDataId.SingleValueId
    fun idFor(id: ColumnId<out Comparable<*>>) = toFileDataIdMapper.idFor(id) as FileDataId.ColumnId

    fun valueType(valueType: KClass<out Any>) = typeMapper.toFile(valueType)
    fun indexType(valueType: KClass<out Any>) = indexTypeMapper.toFile(valueType)

    fun <T: Any> value(valueType: KClass<T>, value: T) = valueMapper.toFile(valueType, value)
}