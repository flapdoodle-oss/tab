package de.flapdoodle.tab.io.csv

data class Format(
    val separator: Char = SEMICOLON,
    val quote: Char = DOUBLE_QUOTE,
) {

    companion object {
        const val SINGLE_QUOTE: Char ='\''
        const val DOUBLE_QUOTE: Char ='\"'

        const val SEMICOLON: Char = ';'
        const val COMMA: Char = ','
        const val TAB: Char = '\t'
        const val COLON: Char = ':'
    }
}