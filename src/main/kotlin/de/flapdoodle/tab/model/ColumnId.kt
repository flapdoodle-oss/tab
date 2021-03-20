package de.flapdoodle.tab.model

import de.flapdoodle.tab.data.ColumnId
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

data class ColumnId<T : Any>(
    val type: KClass<T>,
    val id: Int = idGenerator.nextIdFor(type)
) {
    companion object {
        private val idGenerator = IdGenerator()

        fun <T : Any> create(type: KClass<T>): ColumnId<T> {
            return ColumnId(type, idGenerator.nextIdFor(type))
        }

        inline fun <reified T : Any> create(): ColumnId<T> {
            return create(T::class)
        }
    }
}