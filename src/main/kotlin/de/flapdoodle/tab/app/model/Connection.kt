package de.flapdoodle.tab.app.model

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId

sealed class Connection {
    abstract val name: String
    abstract val id: Id<out Connection>

    data class ColumnConnection<K: Any, V: Any>(
        override val name: String,
        val source: Id<out Node>,
        val columnId: ColumnId<K, V>,
        val destination: Id<Input>,
        override val id: Id<ColumnConnection<*,*>> = Id.nextId(ColumnConnection::class)
    ) : Connection()

    data class AggregateConnection<K: Any, V: Any>(
        override val name: String,
        val source: Id<out Node>,
        val columnId: ColumnId<K, V>,
        val destination: Id<Input>,
        override val id: Id<ColumnConnection<*,*>> = Id.nextId(ColumnConnection::class)
    ) : Connection()

    data class ValueConnection<V: Any>(
        override val name: String,
        val source: Id<out Node>,
        val valueId: SingleValueId<V>,
        val destination: Id<Input>,
        override val id: Id<ValueConnection<*>> = Id.nextId(ValueConnection::class)
    ) : Connection()

}