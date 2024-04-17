package de.flapdoodle.tab.app.io.adapter

import kotlin.reflect.KClass

interface ValueMapper {
    fun <T: Any> toFile(type: KClass<T>, value: T): String
    fun <T: Any> toModel(type: KClass<T>, value: String): T
}