package de.flapdoodle.tab.io.csv

import java.io.InputStreamReader
import java.io.Reader
import java.net.URL

object CSV {
    fun <T> with(name: String, withReader: (Reader) -> T): T {
        return csv(name).openStream().use {
            InputStreamReader(it).use { reader ->
                withReader(reader)
            }
        }
    }

    fun csv(name: String): URL {
        return requireNotNull(CSV::class.java.getResource("/$name")) { "could not open resource $name" }
    }
}