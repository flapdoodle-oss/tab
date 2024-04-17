package de.flapdoodle.tab.app.io.adapter

import java.time.LocalDate
import kotlin.reflect.KClass

data class KeyMapIndexTypeMapper(
    val mapping: List<Pair<KClass<out Comparable<Any>>, String>>
) : IndexTypeMapper {
    private val toFile = mapping.associate { it }
    private val toModel = mapping.associate { it.second to it.first }

    init {
        require(toFile.size == mapping.size) { "to file collisions: $mapping" }
        require(toModel.size == mapping.size) { "to model collisions: $mapping" }
    }

    override fun toFile(type: KClass<out Any>): String {
        return requireNotNull(toFile[type]) { "not defined for $type" }
    }

    override fun toModel(type: String): KClass<out Comparable<Any>> {
        return requireNotNull(toModel[type]) { "unknown $type" }
    }


    companion object {
        fun defaultMapper() = KeyMapIndexTypeMapper(listOf(
            fix(String::class) to "String",
            fix(Int::class) to "Int",
            fix(LocalDate::class) to "LocalDate"
        ))

        fun <T: Comparable<T>> fix(type: KClass<T>): KClass<out Comparable<Any>> {
            return type as KClass<out Comparable<Any>>
        }
    }
}