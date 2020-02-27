package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.values.Variable

data class VariableMapping<T: Any>(
    val columnId: ColumnId<T>,
    val variable: Variable<T>
)