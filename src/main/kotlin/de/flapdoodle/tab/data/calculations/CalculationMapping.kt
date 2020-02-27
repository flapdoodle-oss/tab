package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.NamedColumn

data class CalculationMapping<T : Any>(
    val calculation: Calculation<T>,
    val column: NamedColumn<T>
)