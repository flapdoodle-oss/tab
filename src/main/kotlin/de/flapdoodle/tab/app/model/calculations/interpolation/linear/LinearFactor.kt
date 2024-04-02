package de.flapdoodle.tab.app.model.calculations.interpolation.linear

fun interface LinearFactor<K: Any, F: Any> {
    fun factor(start: K, end: K, offset: K): F

    companion object {
        val IntFactor: LinearFactor<Int, Double> = LinearFactor { start, end, offset -> (offset - start) * 1.0 / (end - start) }
    }
}