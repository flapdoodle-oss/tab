package de.flapdoodle.tab.model

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

class IdGenerator {
    private val idGeneratorMap = ConcurrentHashMap<KClass<out Any>, AtomicInteger>()

    fun nextIdFor(type: KClass<out Any>): Int {
        return idGeneratorMap.getOrPut(type, { AtomicInteger() }).incrementAndGet()
    }
}