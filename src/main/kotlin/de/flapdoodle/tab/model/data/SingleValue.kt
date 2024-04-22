package de.flapdoodle.tab.model.data

import de.flapdoodle.kfx.controls.colors.HashedColors
import javafx.scene.paint.Color
import kotlin.reflect.KClass

data class SingleValue<T : Any>(
    val name: String,
    val valueType: KClass<T>,
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

    companion object {
        fun <K : Any> of(name: String, value: K, id: SingleValueId): SingleValue<K> {
            return SingleValue(name, value::class as KClass<K>, value, id)
        }

        fun ofNull(name: String, id: SingleValueId): SingleValue<Unit> {
            return SingleValue(name, Unit::class, null, id)
        }

    }
}