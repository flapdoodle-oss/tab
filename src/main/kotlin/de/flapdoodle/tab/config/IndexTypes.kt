package de.flapdoodle.tab.config

import java.time.LocalDate
import kotlin.reflect.KClass

object IndexTypes {
    fun all(): List<KClass<out Comparable<*>>> {
        return listOf(
            Int::class, Double::class, LocalDate::class, String::class,
        )
    }
}