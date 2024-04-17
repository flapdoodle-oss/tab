package de.flapdoodle.tab.app.io.adapter

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.io.file.FileDataId
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.DataId
import de.flapdoodle.tab.app.model.data.SingleValueId
import kotlin.reflect.KClass

class ToModelMapping(
    val indexTypeMapper: IndexTypeMapper = KeyMapIndexTypeMapper.defaultMapper(),
    val typeMapper: TypeMapper = KeyMapTypeMapper.defaultMapper(),
    val valueMapper: ValueMapper = KeyMapValueMapper.defaultMapper(),
    val toModelIdMapper: ToModelIdMapper = ToModelIdMapper.justDelegate(),
    val toModelDataIdMapper: ToModelDataIdMapper = ToModelDataIdMapper.justDelegate(indexTypeMapper),
) {
    fun <T : Any> nextId(id: String, type: KClass<T>): Id<T> = toModelIdMapper.nextId(id, type)

    fun nextId(id: FileDataId.SingleValueId) = toModelDataIdMapper.nextId(id) as SingleValueId
    fun nextId(id: FileDataId.ColumnId) = toModelDataIdMapper.nextId(id) as ColumnId<out Comparable<Any>>

    fun valueType(valueType: String) = typeMapper.toModel(valueType)
    fun <T: Any> value(valueType: KClass<T>, value: String) = valueMapper.toModel(valueType, value)

    fun indexType(valueType: String) = indexTypeMapper.toModel(valueType)
}