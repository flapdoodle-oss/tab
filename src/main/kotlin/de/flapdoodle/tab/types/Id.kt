package de.flapdoodle.tab.types

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

@Deprecated("use kfx.id")
data class Id<T : Any> constructor(
    val type: KClass<T>,
    val id: Int
) {
  companion object {
    private val idGeneratorMap = ConcurrentHashMap<KClass<out Any>, AtomicInteger>()

    private fun nextIdFor(type: KClass<out Any>): Int {
      return idGeneratorMap.getOrPut(type, { AtomicInteger() }).incrementAndGet()
    }

    fun <T : Any> create(type: KClass<T>): Id<T> {
      return Id(type, nextIdFor(type))
    }

    inline fun <reified T : Any> create(): Id<T> {
      return create(T::class)
    }
  }
}