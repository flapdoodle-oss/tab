package de.flapdoodle.tab.model.data

import de.flapdoodle.reflection.TypeInfo


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

    fun remove(id: SingleValueId): SingleValues {
        return copy(values = values.filter { it.id != id })
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
        val changedValue = SingleValue.of(oldValue.name, value, oldValue.id)
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

    fun <V: Any> add(id: SingleValueId, valueType: TypeInfo<V>, value: V?): SingleValues {
        val v = value(id)
        require(v.valueType.isAssignable(valueType)) { "valueType mismatch: $valueType != ${v.valueType}"}
        val singleValue = (v as SingleValue<V>).copy(value = value)
        return copy(values = values.map {
            if (it.id==singleValue.id) singleValue else it
        })
    }

    fun set(id: SingleValueId, value: Any?): SingleValues {
        val changed = value(id).setIfTypeMatches(value)
        return copy(values = values.map { if (it.id == changed.id) changed else it })
    }

    operator fun get(id: SingleValueId): Any? {
        return value(id).value
    }

    fun forEach(action: (SingleValue<out Any>) -> Unit) {
        values.forEach(action)
    }

}