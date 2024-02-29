package de.flapdoodle.tab.app.model.data

import kotlin.reflect.KClass

data class SingleValue<T : Any>(
    val name: String,
    val valueType: KClass<out T>,
    val value: T? = null,
    override val id: SingleValueId = SingleValueId(),
): Data()