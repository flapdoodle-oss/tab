package de.flapdoodle.tab.config

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import kotlin.reflect.KClass

object ValueTypes {
    fun all(): List<KClass<out Comparable<*>>> {
        return listOf(
            Int::class, Double::class, BigInteger::class, BigDecimal::class, String::class, LocalDate::class
        )
    }
}