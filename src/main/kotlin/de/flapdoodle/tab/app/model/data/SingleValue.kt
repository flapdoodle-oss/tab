package de.flapdoodle.tab.app.model.data

import kotlin.reflect.KClass

data class SingleValue<T : Any>(
    val name: String,
    val valueType: KClass<out T>,
    val value: T? = null,
    override val id: SingleValueId = SingleValueId(),
): Data() {
    fun set(value: T?): SingleValue<T> {
        return copy(value = value)
    }

    fun setIfTypeMatches(value: Any?): SingleValue<T> {
        require(value == null || valueType.isInstance(value)) { "wrong type: $value is not a ${valueType}"}
        return copy(value = value as T?)
    }
}