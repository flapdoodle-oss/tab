package de.flapdoodle.tab.app.model.data

class SingleValue<T : Any>(
    val type: NamedType<T>,
    val value: T?
)