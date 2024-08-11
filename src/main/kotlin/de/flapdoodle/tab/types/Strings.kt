package de.flapdoodle.tab.types

object Strings {
    fun abbreviate(input: String, maxLength: Int): String {
        return if (input.length <= maxLength) input
        else input.substring(0, maxLength - 2) + ".."
    }
}