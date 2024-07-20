package de.flapdoodle.tab.io.csv

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.io.csv.CSV.with
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Title
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

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
    fun readTransposedHomeassistSample() {
//        val iso8601 = DateTimeFormatter.ISO_INSTANT
        val bigDecimalConverter = CsvConverter(TypeInfo.of(BigDecimal::class.java)) { it ->
            BigDecimal(it)
        }

        val localDateTimeConverter = CsvConverter(TypeInfo.of(LocalDateTime::class.java)) {
            LocalDateTime.ofInstant(Instant.parse(it), ZoneId.systemDefault())
        }

        val table = with("homeassist_energie_export_transposed.csv") { reader ->
            val config = ColumnConfig<LocalDateTime>(
                name = Title("Energy"),
                indexConverter = 0 to localDateTimeConverter,
                converter = mapOf(
                    1 to bigDecimalConverter,
                    2 to bigDecimalConverter,
                    3 to bigDecimalConverter
                ),
                headerRows = 3
            )
            ImportCSV.read(reader, config, Format(Format.COMMA,Format.SINGLE_QUOTE))
        }

        assertThat(table.columns.index())
            .containsExactly(
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0, 0),
                LocalDateTime.of(2024, Month.FEBRUARY, 1, 0, 0, 0),
                LocalDateTime.of(2024, Month.MARCH, 1, 0, 0, 0),
                LocalDateTime.of(2024, Month.APRIL, 1, 0, 0, 0),
                LocalDateTime.of(2024, Month.MAY, 1, 0, 0, 0),
                LocalDateTime.of(2024, Month.JUNE, 1, 0, 0, 0),
                LocalDateTime.of(2024, Month.JULY, 1, 0, 0, 0),
            )

        assertThat(table.columns.columns())
            .hasSize(3)

        assertThat(table.columns.columns()[0].name)
            .isEqualTo(Name("sensor.meter_total_energy_import - grid_consumption - kWh","sensor.meter_total_energy_import"))
        
        assertThat(table.columns.get(table.columns.columns()[0].id, LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0, 0)))
            .isEqualTo(BigDecimal("301.4200000000001"))
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