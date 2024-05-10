package de.flapdoodle.tab.model.calculations.interpolation

import kotlin.reflect.KClass

interface InterpolatorFactoryLookup {
    fun <K: Comparable<K>, V: Any> interpolatorFactoryFor(
        type: InterpolationType,
        indexType: KClass<in K>,
        valueType: KClass<V>
    ): InterpolatorFactory<in K, V>
}