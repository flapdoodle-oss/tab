package de.flapdoodle.tab.core.calculation

fun interface Aggregator<I: Any, C: Any> {
    fun aggregate(list: List<Pair<I,C>>): C?
}