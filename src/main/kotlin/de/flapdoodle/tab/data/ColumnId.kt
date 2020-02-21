package de.flapdoodle.tab.data

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

data class ColumnId<T: Any>(
    val type: KClass<T>,
    val id: Int = nextIdFor(type)
) {
  companion object {
    private val idGeneratorMap = ConcurrentHashMap<KClass<out Any>, AtomicInteger>()

    private fun nextIdFor(type: KClass<out Any>): Int {
      return idGeneratorMap.getOrPut(type, { AtomicInteger() }).incrementAndGet()
    }

    fun <T : Any> create(type: KClass<T>): ColumnId<T> {
      return ColumnId(type, nextIdFor(type))
    }

    inline fun <reified T : Any> create(): ColumnId<T> {
      return create(T::class)
    }
  }
}