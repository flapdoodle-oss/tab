package de.flapdoodle.tab.app.io.adapter

import de.flapdoodle.kfx.types.Id
import kotlin.reflect.KClass

interface ToModelIdMapper {
    fun <T : Any> nextId(id: String, type: KClass<T>): Id<T>

    companion object {

        fun justDelegate() = JustDelegate()

        class JustDelegate : ToModelIdMapper {
            private var typeMap = emptyMap<String, KClass<out Any>>()
            private var map = emptyMap<Pair<String, KClass<out Any>>, Id<out Any>>()

            override fun <T : Any> nextId(id: String, type: KClass<T>): Id<T> {
                val expectedType = typeMap[id]
                require(expectedType == null || expectedType == type) {"$id already mapped to $expectedType != $type"}
                var mapped = map[id to type]
                if (mapped==null) {
                    mapped = Id.nextId(type)
                    map = map + ((id to type) to mapped)
                    typeMap = typeMap + (id to type)
                }
                return mapped as Id<T>
            }

        }
    }
}