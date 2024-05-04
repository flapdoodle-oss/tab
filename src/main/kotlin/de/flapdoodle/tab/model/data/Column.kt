package de.flapdoodle.tab.model.data

import de.flapdoodle.kfx.colors.HashedColors
import javafx.scene.paint.Color
import kotlin.reflect.KClass

data class Column<K : Comparable<K>, V : Any>(
    val name: String,
    val indexType: KClass<in K>,
    val valueType: KClass<V>,
    val values: Map<K, V> = emptyMap(),
    override val id: ColumnId = ColumnId(),
    val color: Color = HashedColors.hashedColor(name.hashCode() + id.hashCode())
): Data() {
    fun add(index: K, value: V?): Column<K, V> {
        return copy(values = if (value != null) values + (index to value) else values - index)
    }

    operator fun get(index: K): V? {
        return values[index]
    }

    fun index() = values.keys

    fun set(newValues: Map<K, out Any>): Column<K, V> {
        var map = values
        newValues.forEach { k, v ->
            if (valueType.isInstance(v)) {
                map = (map - k) + (k to (v as V))
            } else {
                throw IllegalArgumentException("can not set $k:$v to $this")
            }
        }
        return copy(values = map)
    }

    fun moveValue(lastIndex: K, newIndex: K): Column<K, V> {
        require(!values.containsKey(newIndex)) { "destination already set" }
        val value = values[lastIndex]
        return if (value != null)
            copy(values = values - lastIndex + (newIndex to value))
        else
            this
    }
}