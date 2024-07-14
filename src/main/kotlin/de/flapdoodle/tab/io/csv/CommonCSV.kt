package de.flapdoodle.tab.io.csv

import java.io.BufferedReader
import java.io.Reader

object CommonCSV {
    private val knowSeparators = listOf(Format.COMMA, Format.SEMICOLON, Format.TAB, Format.COLON)

    fun guessFormat(reader: Reader): Format {
        val bufferedReader = BufferedReader(reader)
        bufferedReader.use { b ->
            return guessFormat(b.lines().map { line -> inspectLine(line) }.toList())
        }
    }

    private fun guessFormat(inspectedLines: List<LineProperties>): Format {
        val lines = inspectedLines.size

        val validSingleQuotes = inspectedLines.count { it.singleQuotes % 2 == 0 && it.singleQuotes > 0}
        val validDoubleQuotes = inspectedLines.count { it.doubleQuotes % 2 == 0 && it.doubleQuotes > 0}

//        println("lines: $lines")
//        println("validSingleQuotes: $validSingleQuotes")
//        println("validDoubleQuotes: $validDoubleQuotes")

        val separator = guessBestMatchingSeparator(inspectedLines)
//        println("guessed separator: '$separator'")

        return Format(
            separator = separator ?: ';',
            quote = if (validSingleQuotes>validDoubleQuotes) '\'' else '"'
        )
    }

    private fun guessBestMatchingSeparator(lines: List<LineProperties>): Char? {
        var separatorCounts = emptyMap<Char, List<Int>>()

        lines.forEach { line ->
            line.separators.forEach { separatorCount ->
                val list = (separatorCounts[separatorCount.separator] ?: emptyList()) + separatorCount.count
                separatorCounts = separatorCounts + (separatorCount.separator to list)
            }
        }


        val filtered = separatorCounts.map { entry -> entry.key to entry.value.filter { it != 0 } }.toMap()

        val sizeCount = filtered.map { entry ->
            val min = entry.value.minOrNull() ?: 0
            val max = entry.value.maxOrNull() ?: 0
            val size = entry.value.size

            SeparatorStat(entry.key, min, max, size)
        }

//        println(sizeCount)

        val perfectMatch = sizeCount.filter { it.min == it.max && it.count > 0 }.sortedByDescending { it.count }
        return if (perfectMatch.isNotEmpty()) {
//            println(perfectMatch)
            perfectMatch[0].separator
        } else {
            sizeCount.filter { it.count > 0 }.maxByOrNull { it.count }?.separator
        }
    }

    data class LineProperties(
        val singleQuotes: Int,
        val doubleQuotes: Int,
        val separators: List<SeparatorCount> = emptyList()
    )

    data class SeparatorCount(val separator: Char, val count: Int)
    data class SeparatorStat(val separator: Char, val min: Int, val max: Int, val count: Int)

    private fun inspectLine(line: String): LineProperties {
        return LineProperties(
            singleQuotes = line.count { c -> c == '\'' },
            doubleQuotes = line.count { c -> c == '\"' },
            separators = knowSeparators.map { s -> SeparatorCount(s, line.count { c -> c == s }) }
        )
    }
}