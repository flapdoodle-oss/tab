package de.flapdoodle.tab.io.adapter

import kotlin.reflect.KClass

interface IndexTypeMapper {
    fun toFile(type: KClass<out Any>): String
    fun toModel(type: String): KClass<out Comparable<*>>
}