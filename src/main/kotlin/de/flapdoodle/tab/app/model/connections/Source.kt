package de.flapdoodle.tab.app.model.connections

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId

sealed class Source {
    abstract val node: Id<out Node>
    abstract val id: Id<out Source>

    data class ColumnSource<K: Any, V: Any>(
        override val node: Id<out Node>,
        val columnId: ColumnId<K, V>,
        override val id: Id<ColumnSource<*, *>> = Id.nextId(ColumnSource::class)
    ) : Source()

    data class ValueSource<V: Any>(
        override val node: Id<out Node>,
        val valueId: SingleValueId<V>,
        override val id: Id<ValueSource<*>> = Id.nextId(ValueSource::class)
    ) : Source()

}