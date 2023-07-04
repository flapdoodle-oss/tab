package de.flapdoodle.tab.types

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

object TypeCounter {
    private val idGeneratorMap = ConcurrentHashMap<KClass<out Any>, AtomicInteger>()

    fun <T : Any> nextId(type: KClass<T>): Int {
        return idGeneratorMap.getOrPut(type) { AtomicInteger() }.incrementAndGet()
    }

    private val contextTypeCounterMap = ConcurrentHashMap<Pair<KClass<out Any>, KClass<out Any>>, AtomicInteger>()

    fun <C: Any, T: Any> nextId(context: KClass<C>, type: KClass<T>): Int {
        return contextTypeCounterMap.getOrPut(context to type) { AtomicInteger() }.incrementAndGet()
    }
}