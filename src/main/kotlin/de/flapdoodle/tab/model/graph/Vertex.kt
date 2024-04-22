package de.flapdoodle.tab.model.graph

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.SingleValueId

sealed class Vertex {
    abstract val node: Id<out de.flapdoodle.tab.model.Node>

    data class Column(
        override val node: Id<out de.flapdoodle.tab.model.Node>,
        val columnId: ColumnId,
    ) : Vertex()

    data class SingleValue(
        override val node: Id<out de.flapdoodle.tab.model.Node>,
        val valueId: SingleValueId,
    ) : Vertex()

}