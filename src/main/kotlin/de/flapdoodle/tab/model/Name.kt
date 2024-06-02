package de.flapdoodle.tab.model

data class Name(
    val long: String,
    val short: String? = null,
    val description: String? = null,
)
