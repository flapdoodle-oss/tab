package de.flapdoodle.tab.core.calculation.evalex

import de.flapdoodle.tab.core.calculation.Calculator
import de.flapdoodle.tab.core.calculation.ColumnValueLookup
import de.flapdoodle.tab.core.values.ColumnType

data class EvalExCalculator<C: Any>(
    val columnType: ColumnType<C>
): Calculator<C> {
    
    override fun calculate(lookup: ColumnValueLookup): C? {
        TODO("Not yet implemented")
    }
}