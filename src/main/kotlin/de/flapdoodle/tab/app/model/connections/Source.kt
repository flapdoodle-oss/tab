package de.flapdoodle.tab.app.model.connections

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.data.ColumnId
import de.flapdoodle.tab.app.model.data.DataId
import de.flapdoodle.tab.app.model.data.SingleValueId

sealed class Source {
    abstract val node: Id<out Node>
    abstract val id: Id<out Source>

    abstract fun dataId(): DataId

    data class ColumnSource<K: Comparable<K>>(
        override val node: Id<out Node>,
        val columnId: ColumnId<K>,
        override val id: Id<ColumnSource<*>> = Id.nextId(ColumnSource::class)
    ) : Source() {
        override fun dataId(): DataId {
            return columnId
        }
    }

    data class ValueSource(
        override val node: Id<out Node>,
        val valueId: SingleValueId,
        override val id: Id<ValueSource> = Id.nextId(ValueSource::class)
    ) : Source() {
        override fun dataId(): DataId {
            return valueId
        }
    }

}