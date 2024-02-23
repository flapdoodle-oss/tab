package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId

// vermutlich muss man ein Mapping von
// Input -> Variable.name machen
// und immer, wenn eine Variable umbenannt wird, den Input mit neuem Namen
// erzeugen, aber wenn es eine Connection dahin gibt, diese ebenfalls kopieren
sealed class Calculation {
    abstract val name: String
    abstract val formula: Formula

    data class Aggregation<V : Any>(
        override val name: String,
        override val formula: Formula,
        val destination: SingleValueId<V>
    ) : Calculation() {
    }

    data class Tabular<K : Any, V : Any>(
        override val name: String,
        override val formula: Formula,
        val destination: ColumnId<K, V>
    ) : Calculation() {
    }
}