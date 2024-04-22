package de.flapdoodle.tab.model.calculations.interpolation

import de.flapdoodle.tab.model.calculations.interpolation.linear.LinearInterpolation

class LinearInterpolator<K : Comparable<K>, V : Any>(
    private val values: Map<K, V>,
    private val interpolation: LinearInterpolation<K, V>

) : Interpolator<K, V> {
    private val index = values.keys.sorted()

    override fun interpolated(offset: K): V? {
        if (index.size > 1) {
            val idx = index.indexOfLast { it <= offset }
            if (idx != -1) {
                return if (idx + 1 < index.size) {
                    // between idx and idx+1
                    val start = index[idx]
                    val end = index[idx + 1]
                    interpolation.interpolate(start to values[start]!!, end to values[end]!!, offset)
                } else {
                    // after last index
                    val start = index[idx - 1]
                    val end = index[idx]
                    interpolation.interpolate(start to values[start]!!, end to values[end]!!, offset)
                }
            } else {
                // before first index
                val start = index[0]
                require(offset < start) { "$offset >= $start" }
                val end = index[1]
                return interpolation.interpolate(start to values[start]!!, end to values[end]!!, offset)
            }
        }
        if (index.size == 1) {
            return values[index[0]]
        }
        return null
    }
}