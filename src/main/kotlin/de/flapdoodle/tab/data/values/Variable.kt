package de.flapdoodle.tab.data.values

import kotlin.reflect.KClass

data class Variable<T: Any>(
    val type: KClass<T>,
    val name: String
)