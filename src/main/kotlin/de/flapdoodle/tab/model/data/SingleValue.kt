package de.flapdoodle.tab.model.data

import de.flapdoodle.kfx.colors.HashedColors
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Name
import javafx.scene.paint.Color
import kotlin.reflect.KClass

data class SingleValue<T : Any>(
    val name: Name,
    val valueType: TypeInfo<T>,
    val value: T? = null,
    override val id: SingleValueId = SingleValueId(),
    val color: Color = HashedColors.hashedColor(name.hashCode() + id.hashCode())
) : Data() {
    fun set(value: T?): SingleValue<T> {
        return copy(value = value)
    }

    fun setIfTypeMatches(value: Any?): SingleValue<T> {
        require(value == null || valueType.isInstance(value)) { "wrong type: $value is not a ${valueType}" }
        return copy(value = value as T?)
    }

    fun isNull(): Boolean {
        return value==null
    }

    companion object {
        fun <K : Any> of(name: Name, value: K, id: SingleValueId): SingleValue<K> {
            return SingleValue(name, TypeInfo.of(value::class.javaObjectType as Class<K>), value, id)
        }

        @Deprecated("don't use")
        fun <K: Any> ofNull(name: Name, valueType: KClass<K>, id: SingleValueId): SingleValue<K> {
            return SingleValue(name, TypeInfo.of(valueType.javaObjectType), null, id)
        }

        fun <K: Any> ofNullable(name: Name, valueType: TypeInfo<K>, value: K?, id: SingleValueId): SingleValue<K> {
            return SingleValue(name, valueType, value, id)
        }

    }
}