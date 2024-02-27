package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId

sealed class Calculation<K: Comparable<K>> {
    abstract val name: String
    abstract val formula: Formula
    abstract val id: Id<Calculation<*>>

    abstract fun changeFormula(newFormula: String): Calculation<K>

    data class Aggregation<K: Comparable<K>>(
        override val name: String,
        override val formula: Formula,
        val destination: SingleValueId = SingleValueId(),
        override val id: Id<Calculation<*>> = Id.Companion.nextId(Calculation::class)
    ) : Calculation<K>() {

        override fun changeFormula(newFormula: String): Aggregation<K> {
            return copy(formula = formula.change(newFormula))
        }
    }

    data class Tabular<K: Comparable<K>>(
        override val name: String,
        override val formula: Formula,
        val destination: ColumnId<K>,
        override val id: Id<Calculation<*>> = Id.Companion.nextId(Calculation::class)
    ) : Calculation<K>() {

        override fun changeFormula(newFormula: String): Tabular<K> {
            return copy(formula = formula.change(newFormula))
        }
    }
}