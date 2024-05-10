package de.flapdoodle.tab.model.calculations.interpolation

interface InterpolatorFactory<K: Comparable<K>, V: Any> {
    fun interpolatorFor(index: Set<K>, values: Map<out K, V>): Interpolator<K, V>
}