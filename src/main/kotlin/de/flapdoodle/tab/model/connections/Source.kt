package de.flapdoodle.tab.model.connections

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.DataId
import de.flapdoodle.tab.model.data.SingleValueId

sealed class Source {
    abstract val node: Id<out de.flapdoodle.tab.model.Node>
    abstract val id: Id<out Source>

    abstract fun dataId(): DataId

    data class ColumnSource<K: Comparable<K>>(
        override val node: Id<out de.flapdoodle.tab.model.Node>,
        val columnId: ColumnId,
        val indexType: TypeInfo<K>,
        override val id: Id<ColumnSource<*>> = Id.nextId(ColumnSource::class)
    ) : Source() {
        override fun dataId(): DataId {
            return columnId
        }
    }

    data class ValueSource(
        override val node: Id<out de.flapdoodle.tab.model.Node>,
        val valueId: SingleValueId,
        override val id: Id<ValueSource> = Id.nextId(ValueSource::class)
    ) : Source() {
        override fun dataId(): DataId {
            return valueId
        }
    }

}