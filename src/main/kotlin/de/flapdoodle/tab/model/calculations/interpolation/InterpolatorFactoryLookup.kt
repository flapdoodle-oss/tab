package de.flapdoodle.tab.model.calculations.interpolation

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.data.Column

interface InterpolatorFactoryLookup {
    fun <K: Comparable<K>, V: Any> interpolatorFactoryFor(
        type: InterpolationType,
        indexType: TypeInfo<in K>,
        valueType: TypeInfo<V>
    ): InterpolatorFactory<in K, V>
}