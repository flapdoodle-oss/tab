package de.flapdoodle.tab.ui.views.table

import de.flapdoodle.reflection.ClassTypeInfo
import de.flapdoodle.reflection.TypeInfo
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import kotlin.reflect.KClass

object Hack {
    val knownClasses: List<KClass<out Any>> = listOf(
        BigDecimal::class,
        BigInteger::class,
        Double::class,
        Float::class,
        Long::class,
        Int::class,
        LocalDate::class,
        String::class
    )

    fun <T: Any> classOf(type: TypeInfo<T>): KClass<T> {
        require(type is ClassTypeInfo<T>) {""}

        val match = knownClasses.firstOrNull { it.javaObjectType == type.type() || it.java == type.type() }
        requireNotNull(match) { "not implemented: $type"}
        return match as KClass<T>
    }
}