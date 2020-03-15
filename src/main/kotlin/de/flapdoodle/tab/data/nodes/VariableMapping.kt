package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.values.Input

data class VariableMapping<T: Any>(
    val columnConnection: ColumnConnection<T>,
    val variable: Input<T>
)