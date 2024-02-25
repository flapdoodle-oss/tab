package de.flapdoodle.tab.app.model.data

import kotlin.reflect.KClass

data class SingleValue<T : Any>(
    val name: String,
    val valueType: KClass<T>,
    val value: T? = null,
    val id: SingleValueId = SingleValueId(),
): Data()