package de.flapdoodle.tab.io.adapter

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

class KeyMapValueMapper(
    val mapper: List<TypedValueMapper<out Any>>
) : ValueMapper {

    private val byType = mapper.associateBy { it.type }

    override fun <T : Any> toFile(type: KClass<T>, value: T): String {
        val typedMapper = requireNotNull(byType[type]) { "not found for $type" } as TypedValueMapper<T>
        return typedMapper.toFile(value)
    }

    override fun <T : Any> toModel(type: KClass<T>, value: String): T {
        val typedMapper = requireNotNull(byType[type]) { "not found for $type" } as TypedValueMapper<T>
        return typedMapper.toModel(value)
    }

    data class TypedValueMapper<T : Any>(
        val type: KClass<T>,
        val toFile: (T) -> String,
        val toModel: (String) -> T
    )

    companion object {
        fun defaultMapper() = KeyMapValueMapper(
            listOf(
                TypedValueMapper(String::class, { it }, { it }),
                TypedValueMapper(Double::class, { it.toString() }, { java.lang.Double.parseDouble(it) }),
                TypedValueMapper(BigInteger::class, { it.toString() }, { BigInteger(it) }),
                TypedValueMapper(BigDecimal::class, { it.toString() }, { BigDecimal(it) }),
                TypedValueMapper(Int::class, { it.toString() }, { Integer.valueOf(it) }),
                TypedValueMapper(LocalDate::class, { DateTimeFormatter.ISO_LOCAL_DATE.format(it) }, { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }),
            )
        )
    }
}