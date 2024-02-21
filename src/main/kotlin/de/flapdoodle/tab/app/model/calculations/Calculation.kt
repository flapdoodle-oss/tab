package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId

sealed class Calculation {
    abstract fun variableNames(): Set<String>

    data class Aggregation<V : Any>(
        val destination: SingleValueId<V>
    ) : Calculation() {
        override fun variableNames(): Set<String> {
            return emptySet()
        }
    }

    data class Tabular<K : Any, V : Any>(
        val destination: ColumnId<K, V>
    ) : Calculation() {
        override fun variableNames(): Set<String> {
            return emptySet()
        }
    }
}