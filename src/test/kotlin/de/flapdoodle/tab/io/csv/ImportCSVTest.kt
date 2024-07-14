package de.flapdoodle.tab.io.csv

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import com.opencsv.CSVReaderHeaderAwareBuilder
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL

class ImportCSVTest {
    @Test
    fun sample() {
        with("homeassist-energie-export.csv") { reader ->
            ImportCSV.guessProperties(reader)
        }
        with("pv-ertrag-2023.csv") { reader ->
            ImportCSV.guessProperties(reader)
        }
        with("pv-ertrag-2023-variante2.csv") { reader ->
            ImportCSV.guessProperties(reader)
        }
    }

    @Test
    fun readHomeassistSample() {
        val allLines = with("homeassist-energie-export.csv") { reader ->
            val csvReader = CSVReaderBuilder(reader)
                .withCSVParser(
                    CSVParserBuilder()
                        .withSeparator(',')
                        .withQuoteChar('\'')
                        .build()
                )
                .build()
            csvReader.readAll()
        }

        // headers are left, rows are columns
        assertThat(allLines).hasSize(12)
        assertThat(allLines.map { it.size }.toSet())
            .containsExactly(10)

        assertThat(allLines[0][0]).isEqualTo("entity_id")
        assertThat(allLines[11][9]).isEqualTo("0")
    }

    @Test
    fun readSamplePVErtrag() {
        val allLines = with("pv-ertrag-2023.csv") { reader ->
            val csvReader = CSVReaderBuilder(reader)
                .withCSVParser(
                    CSVParserBuilder()
                        .withSeparator(';')
                        .withQuoteChar('\'')
                        .build()
                )
                .build()
            csvReader.readAll()
        }

        // header + 10 lines
        assertThat(allLines).hasSize(11)
        assertThat(allLines.map { it.size }.toSet())
            .containsExactly(14)

        assertThat(allLines[0][0]).isEqualTo("Durchschnittswerte\n" +
                "für PLZ-Bereiche\n" +
                "[kWh/kWp]")
        assertThat(allLines[10][13]).isEqualTo("958")
    }

    @Test
    fun readSamplePVErtragVariante2() {
        val allLines = with("pv-ertrag-2023-variante2.csv") { reader ->
            val csvReader = CSVReaderBuilder(reader)
                .withCSVParser(
                    CSVParserBuilder()
                        .withSeparator(';')
                        .withQuoteChar('\'')
                        .build()
                )
                .build()
            csvReader.readAll()
        }

        // header + 10 lines
        assertThat(allLines).hasSize(11)
        assertThat(allLines.map { it.size }.toSet())
            .containsExactly(14)

        assertThat(allLines[0][0]).isEqualTo("Durchschnittswerte\n" +
                "für PLZ-Bereiche\n" +
                "[kWh/kWp]")
        assertThat(allLines[10][13]).isEqualTo("958")
    }

    private fun <T> with(name: String, withReader: (Reader) -> T): T {
        return csv(name).openStream().use {
            InputStreamReader(it).use { reader ->
                withReader(reader)
            }
        }
    }

    private fun csv(name: String): URL {
        return requireNotNull(ImportCSV.javaClass.getResource("/$name")) { "could not open resource $name" }
    }
}