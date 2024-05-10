package de.flapdoodle.tab.io.adapter

import de.flapdoodle.reflection.TypeInfo
import kotlin.reflect.KClass

interface ValueMapper {
    fun <T: Any> toFile(type: TypeInfo<T>, value: T): String
    fun <T: Any> toModel(type: TypeInfo<T>, value: String): T
}