package de.flapdoodle.tab.app.model.calculations.types

import de.flapdoodle.eval.core.evaluables.Parameter
import de.flapdoodle.reflection.MapTypeInfo
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.app.model.data.Column
import java.math.BigDecimal
import kotlin.reflect.KClass

data class IndexMap<K: Comparable<K>, V: Any>(
    private val map: Map<K, V>,
    private val indexType: KClass<in K>,
    private val valueType: KClass<V>
) {
    private val orderedValues = map.entries.sortedBy { it.key }.map { it.value }

    fun keys() = map.keys
    fun values() = orderedValues

    companion object {
        fun <K: Comparable<K>, V: Any>asMap(column: Column<K, V>): IndexMap<K, V> {
            return IndexMap(column.values, column.indexType, column.valueType)
        }

        fun <K: Any> asParameterWithValueType(type: KClass<K>): Parameter<IndexMap<*, K>> {
            return Parameter.of(IndexMapTypeInfo(
                TypeInfo.of(type.javaObjectType)
            ))
        }
    }

    data class IndexMapTypeInfo<V: Any>(
        private val valueType: TypeInfo<V>
    ) : TypeInfo<IndexMap<*, V>> {
        override fun cast(instance: Any?): IndexMap<*, V> {
            require(isInstance(instance)) { "wrong type: $instance" }
            return instance as IndexMap<*, V>
        }

        override fun isInstance(instance: Any?): Boolean {
            return instance is IndexMap<*,*> && instance.values().all { valueType.isInstance(it) }
        }

    }
}