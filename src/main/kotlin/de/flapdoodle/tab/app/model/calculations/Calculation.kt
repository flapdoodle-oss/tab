package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.HasName
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValue
import de.flapdoodle.tab.app.model.data.SingleValueId
import kotlin.reflect.KClass

sealed class Calculation<K: Comparable<K>>(
    private val indexType: KClass<K>,
    private val name: String,
    private val formula: Formula,
    open val id: Id<Calculation<*>>
): HasName {
    // TODO verschieben?
//    abstract val id: Id<Calculation<*>>

    fun indexType() = indexType
    override fun name() = name
    fun formula() = formula
    
    fun evaluate(values: Map<Variable, Any?>) = formula.evaluate(values)
    fun variables() = formula.variables()

    abstract fun changeFormula(newFormula: String): Calculation<K>

    data class Aggregation<K: Comparable<K>>(
        private val indexType: KClass<K>,
        private val name: String,
        private val formula: Formula,
        private val destination: SingleValueId = SingleValueId(),
        override val id: Id<Calculation<*>> = Id.nextId(Calculation::class)
    ) : Calculation<K>(indexType, name, formula, id) {

        fun destination() = destination

        override fun changeFormula(newFormula: String): Aggregation<K> {
            return copy(formula = formula.change(newFormula))
        }
    }

    data class Tabular<K: Comparable<K>>(
        private val indexType: KClass<K>,
        private val name: String,
        private val formula: Formula,
        private val destination: ColumnId = ColumnId(),
        override val id: Id<Calculation<*>> = Id.nextId(Calculation::class)
    ) : Calculation<K>(indexType, name, formula, id) {

        fun destination() = destination

        override fun changeFormula(newFormula: String): Tabular<K> {
            return copy(formula = formula.change(newFormula))
        }
    }
}