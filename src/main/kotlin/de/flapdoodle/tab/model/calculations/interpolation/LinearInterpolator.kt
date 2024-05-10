package de.flapdoodle.tab.model.calculations.interpolation

import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.tab.model.calculations.interpolation.linear.LinearInterpolation
import kotlin.reflect.KClass

class LinearInterpolator<K : Comparable<K>, V : Any>(
    private val values: Map<K, V>,
    private val interpolation: LinearInterpolation<K, V>,
    private val valueType: KClass<V>
) : Interpolator<K, V> {
    private val index = values.keys.sorted()

    override fun interpolated(offset: K): Evaluated<V> {
        if (index.size > 1) {
            val idx = index.indexOfLast { it <= offset }
            if (idx != -1) {
                return if (idx + 1 < index.size) {
                    // between idx and idx+1
                    val start = index[idx]
                    val end = index[idx + 1]
                    Evaluated.ofNullable(valueType.java, interpolation.interpolate(start to values[start]!!, end to values[end]!!, offset))
                } else {
                    // after last index
                    val start = index[idx - 1]
                    val end = index[idx]
                    Evaluated.ofNullable(valueType.java, interpolation.interpolate(start to values[start]!!, end to values[end]!!, offset))
                }
            } else {
                // before first index
                val start = index[0]
                require(offset < start) { "$offset >= $start" }
                val end = index[1]
                return Evaluated.ofNullable(valueType.java, interpolation.interpolate(start to values[start]!!, end to values[end]!!, offset))
            }
        }
        if (index.size == 1) {
            return Evaluated.ofNullable(valueType.java, values[index[0]])
        }
        return Evaluated.ofNull(valueType.java)
    }
}