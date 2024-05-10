package de.flapdoodle.tab.io.adapter

import de.flapdoodle.reflection.TypeInfo
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

data class KeyMapTypeMapper(
    val mapping: List<Pair<TypeInfo<out Any>, String>>
) : TypeMapper {
    private val toFile = mapping.associate { it }
    private val toModel = mapping.associate { it.second to it.first }

    init {
        require(toFile.size == mapping.size) { "to file collisions: $mapping" }
        require(toModel.size == mapping.size) { "to model collisions: $mapping" }
    }

    override fun toFile(type: TypeInfo<out Any>): String {
        return requireNotNull(toFile[type]) { "not defined for $type" }
    }

    override fun toModel(type: String): TypeInfo<out Any> {
        return requireNotNull(toModel[type]) { "unknown $type" }
    }


    companion object {
        fun defaultMapper() = KeyMapTypeMapper(listOf(
            TypeInfo.of(String::class.java) to "String",
            TypeInfo.of(Double::class.javaObjectType) to "Double",
            TypeInfo.of(BigDecimal::class.java) to "BigDecimal",
            TypeInfo.of(BigInteger::class.java) to "BigInteger",
            TypeInfo.of(Int::class.javaObjectType) to "Int"
        ))
    }
}