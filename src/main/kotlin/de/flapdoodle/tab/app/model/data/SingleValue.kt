package de.flapdoodle.tab.app.model.data

data class SingleValue<T : Any>(
    val name: String,
    val id: SingleValueId<T>,
    val value: T? = null,
)