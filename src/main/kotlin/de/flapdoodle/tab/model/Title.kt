package de.flapdoodle.tab.model

data class Title(
    val long: String,
    val short: String? = null,
    val description: String? = null,
)