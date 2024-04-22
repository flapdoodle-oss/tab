package de.flapdoodle.tab.model.calculations.interpolation

fun interface Interpolator<K: Any, V: Any> {
    fun interpolated(offset: K): V?

    fun interpolatedAt(index: Set<K>): Map<K, V?> {
        return interpolate(index, this)
    }

    companion object {
        fun <K: Comparable<K>, V: Any> valueAtOffset(values: Map<K, V>): Interpolator<K, V> {
            val index = values.keys.toSortedSet()
            return Interpolator { offset ->
                val firstIndex = index.lastOrNull {
                    it <= offset
                }
                if (firstIndex!=null) values[firstIndex] else null
            }
        }

        fun <K: Any, V: Any> interpolate(index: Set<K>, interpolator: Interpolator<K, V>): Map<K, V?> {
            return index.associateWith { interpolator.interpolated(it) }
        }
    }
}