package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.HasName
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId

sealed class Calculation<K: Comparable<K>>(
    private val name: String,
    private val formula: Formula,
): HasName {
    abstract val id: Id<Calculation<*>>

    override fun name() = name
//    fun formula() = formula
    fun evaluate(values: Map<Variable, Any?>) = formula.evaluate(values)
    fun variables() = formula.variables()

    abstract fun changeFormula(newFormula: String): Calculation<K>

    data class Aggregation<K: Comparable<K>>(
        private val name: String,
        private val formula: Formula,
        private val destination: SingleValueId = SingleValueId(),
        override val id: Id<Calculation<*>> = Id.Companion.nextId(Calculation::class)
    ) : Calculation<K>(name, formula) {

        fun destination() = destination

        override fun changeFormula(newFormula: String): Aggregation<K> {
            return copy(formula = formula.change(newFormula))
        }
    }

    data class Tabular<K: Comparable<K>>(
        private val name: String,
        private val formula: Formula,
        private val destination: ColumnId<K>,
        override val id: Id<Calculation<*>> = Id.Companion.nextId(Calculation::class)
    ) : Calculation<K>(name, formula) {

        fun destination() = destination

        override fun changeFormula(newFormula: String): Tabular<K> {
            return copy(formula = formula.change(newFormula))
        }
    }
}