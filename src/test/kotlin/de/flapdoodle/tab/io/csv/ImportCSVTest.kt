package de.flapdoodle.tab.io.csv

import de.flapdoodle.tab.io.csv.CSV.with
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ImportCSVTest {

    @Test
    fun readHomeassistSample() {
        val allLines = with("homeassist-energie-export.csv") { reader ->
            ImportCSV.read(reader, Format(Format.COMMA,Format.SINGLE_QUOTE))
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
            ImportCSV.read(reader, Format(Format.SEMICOLON,Format.SINGLE_QUOTE))
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
            ImportCSV.read(reader, Format(Format.SEMICOLON,Format.SINGLE_QUOTE))
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
}