package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.values.Variable

data class VariableMapping<T: Any>(
    val columnConnection: ColumnConnection<T>,
    val variable: Variable<T>
)