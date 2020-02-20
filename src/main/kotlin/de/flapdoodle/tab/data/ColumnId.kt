package de.flapdoodle.tab.data

import kotlin.reflect.KClass

data class ColumnId<T: Any>(val type: KClass<T>, val name: String)