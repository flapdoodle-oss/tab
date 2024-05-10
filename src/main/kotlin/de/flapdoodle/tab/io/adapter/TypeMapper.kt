package de.flapdoodle.tab.io.adapter

import de.flapdoodle.reflection.TypeInfo
import kotlin.reflect.KClass

interface TypeMapper {
    fun toFile(type: TypeInfo<out Any>): String
    fun toModel(type: String): TypeInfo<out Any>
}