package de.flapdoodle.tab.app.model.calculations.interpolation.linear

import java.math.BigDecimal

fun interface FactorMultiplicator<V: Any, F: Any> {
    fun multiply(start: V, end: V, factor: F): V

    companion object {
        val IntDoubleMultiplicator: FactorMultiplicator<Int, Double> = FactorMultiplicator { start, end, factor ->
            start + ((end - start) * factor).toInt()
        }
        val BigDecimalDoubleMultiplicator: FactorMultiplicator<BigDecimal, Double> = FactorMultiplicator { start, end, factor ->
            start + (end - start).multiply(factor.toBigDecimal())
        }
    }
}