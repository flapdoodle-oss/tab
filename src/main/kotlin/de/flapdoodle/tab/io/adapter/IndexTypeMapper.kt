package de.flapdoodle.tab.io.adapter

import de.flapdoodle.reflection.TypeInfo

interface IndexTypeMapper {
    fun toFile(type: TypeInfo<out Any>): String
    fun toModel(type: String): TypeInfo<out Comparable<*>>
}