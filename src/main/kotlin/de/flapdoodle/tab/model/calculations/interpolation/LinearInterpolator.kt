package de.flapdoodle.tab.model.calculations.interpolation

import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.tab.model.calculations.interpolation.linear.LinearInterpolation
import kotlin.reflect.KClass

class LinearInterpolator<K : Comparable<K>, V : Any>(
    private val values: Map<out K, V>,
    private val interpolation: LinearInterpolation<in K, V>,
    private val valueType: KClass<V>
) : Interpolator<K, V> {
    private val _index = values.keys.sorted()

    override fun interpolated(offset: K): Evaluated<V> {
        if (_index.size > 1) {
            val idx = _index.indexOfLast { it <= offset }
            if (idx != -1) {
                return if (idx + 1 < _index.size) {
                    // between idx and idx+1
                    val start = _index[idx]
                    val end = _index[idx + 1]
                    Evaluated.ofNullable(valueType.javaObjectType, interpolation.interpolate(start to values[start]!!, end to values[end]!!, offset))
                } else {
                    // after last index
                    val start = _index[idx - 1]
                    val end = _index[idx]
                    Evaluated.ofNullable(valueType.javaObjectType, interpolation.interpolate(start to values[start]!!, end to values[end]!!, offset))
                }
            } else {
                // before first index
                val start = _index[0]
                require(offset < start) { "$offset >= $start" }
                val end = _index[1]
                return Evaluated.ofNullable(valueType.javaObjectType, interpolation.interpolate(start to values[start]!!, end to values[end]!!, offset))
            }
        }
        if (_index.size == 1) {
            return Evaluated.ofNullable(valueType.javaObjectType, values[_index[0]])
        }
        return Evaluated.ofNull(valueType.javaObjectType)
    }
}