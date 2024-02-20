package de.flapdoodle.tab.app.model.data

data class SingleValues(
    val values: List<SingleValue<*>> = emptyList()
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

    fun <V : Any> value(id: SingleValueId<V>): SingleValue<V> {
        return requireNotNull(valueIdMap[id]) { "value $id not found" } as SingleValue<V>
    }

    fun <V: Any> add(id: SingleValueId<V>, value: V?): SingleValues {
        val singleValue = value(id).copy(value = value)
        return copy(values = values.map {
            if (it.id==singleValue.id) singleValue else it
        })
    }

    operator fun <V: Any> get(id: SingleValueId<V>): V? {
        return value(id).value
    }

}