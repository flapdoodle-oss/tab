package de.flapdoodle.tab.app.model

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.SingleValueId

// TODO vermutlich wird das anders..
// eine Node hat zu jedem Input eine optionale Quelle
sealed class Connection {
    abstract val name: String
    abstract val id: Id<out Connection>

    data class ColumnConnection<K: Any, V: Any>(
        override val name: String,
        val source: Id<out Node>,
        val columnId: ColumnId<K, V>,
        val destination: Id<out Node>,
        val input: Id<Input>,
        override val id: Id<ColumnConnection<*,*>> = Id.nextId(ColumnConnection::class)
    ) : Connection()

    data class ValueConnection<V: Any>(
        override val name: String,
        val source: Id<out Node>,
        val valueId: SingleValueId<V>,
        val destination: Id<out Node>,
        val input: Id<Input>,
        override val id: Id<ValueConnection<*>> = Id.nextId(ValueConnection::class)
    ) : Connection()

}