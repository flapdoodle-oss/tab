package de.flapdoodle.tab.model

data class Name(
    val long: String,
    val short: String? = null
) {
    fun shortest(): String {
        return if (!short.isNullOrEmpty()) short else long
    }
}
