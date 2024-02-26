package de.flapdoodle.tab.app.model.data

import kotlin.reflect.KClass

data class SingleValues(
    val values: List<SingleValue<out Any>> = emptyList()
) {
    init {
        val collisions = values.groupBy { it.id }
            .filter { it.value.size > 1 }
            .keys
        require(collisions.isEmpty()) { "same id used more than once: $collisions"}
    }
    private val valueIdMap by lazy { values.associateBy { it.id } }

    fun addValue(value: SingleValue<*>): SingleValues {
        return copy(values = values + value)
    }

    fun value(id: SingleValueId): SingleValue<out Any> {
        return requireNotNull(valueIdMap[id]) { "value $id not found" }
    }

    fun find(id: SingleValueId): SingleValue<out Any>? {
        return valueIdMap[id]
    }

    @Deprecated("use change with mapper")
    fun <T: Any> changeWithType(id: SingleValueId, value: T): SingleValues {
        val oldValue = value(id)
        val changedValue = SingleValue(oldValue.name, value::class, value, oldValue.id)
        return copy(values = values.map { if (it.id==id) changedValue else it })
    }

    @Deprecated("use change with mapper")
    fun <T: Any> change(id: SingleValueId, value: T?): SingleValues {
        val oldValue = value(id)
        val changedValue = if (value!=null) {
            require(oldValue.valueType.isInstance(value)) { "type mismatch: ${oldValue.valueType} != $value"}
            (oldValue as SingleValue<T>).copy(value = value)
        } else
            oldValue.copy(value = null)
        return copy(values = values.map { if (it.id==id) changedValue else it })
    }
    
    fun change(id: SingleValueId, map: (SingleValue<out Any>) -> SingleValue<out Any>): SingleValues {
        return copy(values = values.map { if (it.id==id) map(it) else it })
    }

    fun <V: Any> add(id: SingleValueId, valueType: KClass<V>, value: V?): SingleValues {
        val v = value(id)
        require(v.valueType == valueType) { "valueType mismatch: $valueType != ${v.valueType}"}
        val singleValue = (v as SingleValue<V>).copy(value = value)
        return copy(values = values.map {
            if (it.id==singleValue.id) singleValue else it
        })
    }

    operator fun get(id: SingleValueId): Any? {
        return value(id).value
    }

    fun forEach(action: (SingleValue<out Any>) -> Unit) {
        values.forEach(action)
    }
}