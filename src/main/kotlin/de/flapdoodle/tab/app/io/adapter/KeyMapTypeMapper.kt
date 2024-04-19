package de.flapdoodle.tab.app.io.adapter

import kotlin.reflect.KClass

data class KeyMapTypeMapper(
    val mapping: List<Pair<KClass<out Any>, String>>
) : TypeMapper {
    private val toFile = mapping.associate { it }
    private val toModel = mapping.associate { it.second to it.first }

    init {
        require(toFile.size == mapping.size) { "to file collisions: $mapping" }
        require(toModel.size == mapping.size) { "to model collisions: $mapping" }
    }

    override fun toFile(type: KClass<out Any>): String {
        return requireNotNull(toFile[type]) { "not defined for $type" }
    }

    override fun toModel(type: String): KClass<out Any> {
        return requireNotNull(toModel[type]) { "unknown $type" }
    }


    companion object {
        fun defaultMapper() = KeyMapTypeMapper(listOf(
            String::class to "String",
            Double::class to "Double",
            Int::class to "Int"
        ))
    }
}