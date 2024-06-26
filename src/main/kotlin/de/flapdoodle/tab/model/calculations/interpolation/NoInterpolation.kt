package de.flapdoodle.tab.model.calculations.interpolation

import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.reflection.TypeInfo

class NoInterpolation<K: Comparable<K>, V: Any>(
    private val valueType: TypeInfo<V>
) : InterpolatorFactory<K, V> {
    override fun interpolatorFor(index: Set<K>, values: Map<out K, V>): Interpolator<K, V> {
        return Interpolator {
            Evaluated.ofNullable(valueType, values[it])
        }
    }
}