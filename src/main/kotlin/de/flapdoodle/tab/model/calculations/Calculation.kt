package de.flapdoodle.tab.model.calculations

import de.flapdoodle.eval.core.Expression
import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.SingleValueId
import kotlin.reflect.KClass

sealed class Calculation<K: Comparable<K>>(
    private val indexType: TypeInfo<K>,
    private val name: Name,
    private val formula: Formula,
    open val id: Id<Calculation<*>>
) {
    // TODO verschieben?
//    abstract val id: Id<Calculation<*>>

    fun indexType() = indexType
    fun name() = name
    fun formula() = formula

    fun evaluate(values: Map<Variable, Evaluated<out Any>>) = formula.evaluate(values)
    fun evaluateType(values: Map<Variable, Evaluated<out Any>>) = formula.evaluateType(values)
    fun variables() = formula.variables()

    abstract fun changeFormula(name: Name, newExpression: Expression): Calculation<K>

    data class Aggregation<K: Comparable<K>>(
        private val indexType: TypeInfo<K>,
        private val name: Name,
        private val formula: Formula,
        private val destination: SingleValueId = SingleValueId(),
        override val id: Id<Calculation<*>> = Id.nextId(Calculation::class)
    ) : Calculation<K>(indexType, name, formula, id) {

        fun destination() = destination

        override fun changeFormula(name: Name, newExpression: Expression): Aggregation<K> {
            return copy(name = name, formula = formula.change(newExpression))
        }
    }

    data class Tabular<K: Comparable<K>>(
        private val indexType: TypeInfo<K>,
        private val name: Name,
        private val formula: Formula,
        private val interpolationType: InterpolationType = InterpolationType.Linear,
        private val destination: ColumnId = ColumnId(),
        override val id: Id<Calculation<*>> = Id.nextId(Calculation::class)
    ) : Calculation<K>(indexType, name, formula, id) {

        fun destination() = destination

        fun interpolationType() = interpolationType

        override fun changeFormula(name: Name, newExpression: Expression): Tabular<K> {
            return copy(name = name, formula = formula.change(newExpression))
        }
    }
}