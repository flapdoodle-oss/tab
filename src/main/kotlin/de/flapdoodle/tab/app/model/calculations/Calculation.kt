package de.flapdoodle.tab.app.model.calculations

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId

// vermutlich muss man ein Mapping von
// Input -> Variable.name machen
// und immer, wenn eine Variable umbenannt wird, den Input mit neuem Namen
// erzeugen, aber wenn es eine Connection dahin gibt, diese ebenfalls kopieren
sealed class Calculation {
    abstract val name: String
    abstract val formula: Formula
    abstract val id: Id<Calculation>

    abstract fun changeFormula(newFormula: String): Calculation

    data class Aggregation(
        override val name: String,
        override val formula: Formula,
        val destination: SingleValueId = SingleValueId(),
        override val id: Id<Calculation> = Id.Companion.nextId(Calculation::class)
    ) : Calculation() {

        override fun changeFormula(newFormula: String): Aggregation {
            return copy(formula = formula.change(newFormula))
        }
    }

    data class Tabular(
        override val name: String,
        override val formula: Formula,
        val destination: ColumnId = ColumnId(),
        override val id: Id<Calculation> = Id.Companion.nextId(Calculation::class)
    ) : Calculation() {

        override fun changeFormula(newFormula: String): Tabular {
            return copy(formula = formula.change(newFormula))
        }
    }
}