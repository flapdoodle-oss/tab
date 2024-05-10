package de.flapdoodle.tab.model.calculations.interpolation

import de.flapdoodle.tab.model.calculations.interpolation.linear.LinearInterpolation
import kotlin.reflect.KClass

class LinearInterpolatorFactory<K : Comparable<K>, V : Any>(
    val valueType: KClass<V>,
    val interpolation: LinearInterpolation<in K, V>,
) : InterpolatorFactory<K, V> {
    override fun interpolatorFor(index: Set<K>, values: Map<out K, V>): Interpolator<K, V> {
        return LinearInterpolator(values, interpolation, valueType)
    }
}