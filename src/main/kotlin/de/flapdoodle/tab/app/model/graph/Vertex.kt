package de.flapdoodle.tab.app.model.graph

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId

sealed class Vertex {
    abstract val node: Id<out Node>

    data class Column(
        override val node: Id<out Node>,
        val columnId: ColumnId,
    ) : Vertex()

    data class SingleValue(
        override val node: Id<out Node>,
        val valueId: SingleValueId,
    ) : Vertex()

}