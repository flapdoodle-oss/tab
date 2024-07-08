package de.flapdoodle.tab.model.errors

data class Error(
    val startPosition: Int,
    val endPosition: Int,
    val tokenString: String,
    // TODO make it i18n
    val message: String,
)