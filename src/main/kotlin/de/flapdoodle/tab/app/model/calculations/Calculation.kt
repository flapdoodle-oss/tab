package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId

sealed class Calculation {
    abstract val formula: Formula

    data class Aggregation<V : Any>(
        override val formula: Formula,
        val destination: SingleValueId<V>
    ) : Calculation() {
    }

    data class Tabular<K : Any, V : Any>(
        override val formula: Formula,
        val destination: ColumnId<K, V>
    ) : Calculation() {
    }
}