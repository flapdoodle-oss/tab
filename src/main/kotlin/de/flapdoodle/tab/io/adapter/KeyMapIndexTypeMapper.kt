package de.flapdoodle.tab.io.adapter

import de.flapdoodle.reflection.TypeInfo
import java.time.LocalDate
import kotlin.reflect.KClass

data class KeyMapIndexTypeMapper(
    val mapping: List<Pair<TypeInfo<out Comparable<Any>>, String>>
) : IndexTypeMapper {
    private val toFile = mapping.associate { it }
    private val toModel = mapping.associate { it.second to it.first }

    init {
        require(toFile.size == mapping.size) { "to file collisions: $mapping" }
        require(toModel.size == mapping.size) { "to model collisions: $mapping" }
    }

    override fun toFile(type: TypeInfo<out Any>): String {
        return requireNotNull(toFile[type]) { "not defined for $type" }
    }

    override fun toModel(type: String): TypeInfo<out Comparable<Any>> {
        return requireNotNull(toModel[type]) { "unknown $type" }
    }


    companion object {
        fun defaultMapper() = KeyMapIndexTypeMapper(listOf(
            fix(String::class) to "String",
            fix(Int::class) to "Int",
            fix(LocalDate::class) to "LocalDate"
        ))

        fun <T: Comparable<T>> fix(type: KClass<T>): TypeInfo<out Comparable<Any>> {
            return TypeInfo.of(type.javaObjectType as Class<out Comparable<Any>>)
        }
    }
}