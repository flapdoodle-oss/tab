package de.flapdoodle.tab.model.calculations.interpolation

import de.flapdoodle.eval.core.evaluables.Evaluated
import kotlin.reflect.KClass

class NoInterpolation<K: Comparable<K>, V: Any>(
    private val valueType: KClass<V>
) : InterpolatorFactory<K, V> {
    override fun interpolatorFor(index: Set<K>, values: Map<out K, V>): Interpolator<K, V> {
        return Interpolator {
            Evaluated.ofNullable(valueType.java, values[it])
        }
    }
}